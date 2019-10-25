package com.infoworks.lab.rest.breaker;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.Entity;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractCircuitBreaker<T extends AutoCloseable> implements CircuitBreaker<T> {
    /**
     * Let’s face it, all services will fail or falter at some point in time.
     * Circuit breakers allow your system to handle these failures gracefully.
     * The circuit breaker concept is straightforward.
     * It wraps a function with a monitor that tracks failures.
     * The circuit breaker has 3 distinct states, Closed, Open, and Half-Open:
     *
     *     Closed – When everything is normal, the circuit breaker remains in the closed state and all calls pass through to the services.
     *          When the number of failures exceeds a predetermined threshold the breaker trips, and it goes into the Open state.
     *
     *     Open – The circuit breaker returns an error for calls without executing the function.
     *
     *     Half-Open – After a timeout period, the circuit switches to a half-open state to test if the underlying problem still exists.
     *          If a single call fails in this half-open state, the breaker is once again tripped.
     *          If it succeeds, the circuit breaker resets back to the normal closed state.
     *
     */

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Status _status = Status.CLOSED;
    private final Integer failureThreshold; //Minimum n times (0...n); After this amount of try circuit trips to OPEN state.
    private final Long timeout;             //Http request timeout limit.
    private final Long retryTimePeriod;     //After this time period, the circuit switches to a half-open state to test if the underlying problem still exists.
    private Long lastFailureTime = null;    //
    private Integer failureCount = 0;
    protected final ReentrantLock reLock = new ReentrantLock(true);

    /**
     *
     * @param timeout in milli-second
     * @param failureThreshold in integer count
     * @param retryTimePeriod in milli-second
     */
    public AbstractCircuitBreaker(long timeout, Integer failureThreshold, long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
        this.retryTimePeriod = retryTimePeriod;
    }

    private Invocation _invocation;
    private Invocation.Method _method = Invocation.Method.GET;

    protected abstract Invocation createInvocation(Invocation invocation, Invocation.Method method, Entity data);

    @Override
    public T call(Invocation invocation, Invocation.Method method, Entity data) {

        if (invocation == null && _invocation == null) return null;

        if (invocation != null && invocation.getClass().isAssignableFrom(SimpleWebInvocation.class)){
            ((SimpleWebInvocation)invocation).setTimeout(timeout.intValue());
        }

        //FIXME: Need to test: Will Effective when same circuit breaker would call fromm different thread
        /*if (_invocation != null && _invocation.equals(invocation)){
            invocation = null; //So that, we can use existing invocation repeatedly.
        }*/

        if (invocation != null) _invocation = invocation;
        if (method != null) _method = method;

        T response = null;

        switch (_status){
            case OPEN:
                startObserving(_invocation, _method, data);
                break;
            case HALF_OPEN:
                startMonitoring(_invocation, _method, data);
                break;
            case CLOSED:
                response = circuitTrips(_invocation, _method, data);
                break;
        }

        return response;
    }

    @Override
    public Status online(Invocation invocation, Invocation.Method method, Entity data) {

        if (invocation == null && _invocation == null) return Status.OPEN;

        if (invocation != null && invocation.getClass().isAssignableFrom(SimpleWebInvocation.class)){
            ((SimpleWebInvocation)invocation).setTimeout(timeout.intValue());
        }

        /*if (_invocation != null && _invocation.equals(invocation)){
            return _status;
        }*/

        if (invocation != null) _invocation = invocation;
        if (method != null) _method = method;

        switch (_status){
            case OPEN:
                startObserving(_invocation, _method, data);
                break;
            case HALF_OPEN:
                startMonitoring(_invocation, _method, data);
                break;
            case CLOSED:
                circuitTrips(_invocation, _method, data);
                break;
        }
        return _status;
    }

    protected Integer parseCode(T response){
        return HttpURLConnection.HTTP_NOT_FOUND;
    }

    protected final T circuitTrips(Invocation invocation, Invocation.Method method, Entity data) {
        //
        T response = null;
        do{
            try{
                response = circuitTest(invocation, method, data);
                //
                if (!isAcceptedResponse(response)){
                    recordFailure();
                }
            }catch (HttpInvocationException e) {
                logger.log(Level.INFO, e.getMessage());//REMOVE
                recordFailure();
            }finally {
                updateStatus();
                invocation = createInvocation(invocation, method, data);
                if (_status == Status.OPEN) startObserving(invocation, method, data);
            }
            if (failureCount > failureThreshold) break;
        }while (!isAcceptedResponse(response));
        return response;
    }

    protected abstract boolean isAcceptedResponse(T response);
    protected abstract T circuitTest(Invocation invocation, Invocation.Method method, Entity data) throws HttpInvocationException;

    protected final void updateStatus(){
        reLock.lock();
        try {
            if (failureCount > failureThreshold){
                long nowEpocTime = new Date().getTime();
                long timeElapsed = (nowEpocTime - lastFailureTime);
                boolean isPassedRetryPeriod = timeElapsed > retryTimePeriod;
                if (isPassedRetryPeriod){
                    _status = Status.HALF_OPEN;
                }else{
                    _status = Status.OPEN;
                }
            }else {
                _status = Status.CLOSED;
            }
        } finally {
            reLock.unlock();
        }
        logger.log(Level.INFO, "Circuit Breaker is " + _status.name());//REMOVED
    }

    protected final void reset(){
        reLock.lock();
        try {
            _status = Status.CLOSED;
            failureCount = 0;
            lastFailureTime = null;
        } finally {
            reLock.unlock();
        }
    }

    protected final void recordFailure(){
        reLock.lock();
        try {
            failureCount += 1;
            lastFailureTime = (new Date()).getTime();
        } finally {
            reLock.unlock();
        }
    }

    private ExecutorService _executor = Executors.newSingleThreadExecutor();

    @Override
    public void close() {
        reLock.lock();
        try {
            if (_futureOfObserving != null && _futureOfObserving.isCancelled() == false){
                _futureOfObserving.cancel(true);
            }
            if (_futureOfMonitoring != null && _futureOfMonitoring.isCancelled() == false){
                _futureOfMonitoring.cancel(true);
            }
            if (_executor.isShutdown() == false) {
                _executor.shutdownNow();
            }
        }finally {
            reLock.unlock();
        }
    }

    private Future _futureOfObserving = null;
    protected final void startObserving(Invocation invocation, Invocation.Method method, Entity data){
        if (_futureOfObserving != null && _futureOfObserving.isDone() == false)
            return;
        _futureOfObserving = _executor.submit(() -> {
            logger.log(Level.INFO, "Start...Observing");//REMOVED
            while (true){
                try {
                    Thread.sleep(this.retryTimePeriod.intValue());
                } catch (InterruptedException e) {}
                //
                updateStatus();
                if (_status == Status.HALF_OPEN) {
                    startMonitoring(invocation, method, data);
                    break;
                }
            }
            logger.log(Level.INFO, "End...Observing");//REMOVED
        });
    }

    private Future _futureOfMonitoring = null;
    protected final void startMonitoring(Invocation invocation, Invocation.Method method, Entity data) {
        if (_futureOfMonitoring != null && _futureOfMonitoring.isDone() == false)
            return;
        _futureOfMonitoring = _executor.submit(() -> {
            logger.log(Level.INFO, "Start...Monitoring");//REMOVED
            try{
                T response = circuitTest(invocation, method, data);
                //
                if (isAcceptedResponse(response)){
                    reset();
                }else {
                    recordFailure();
                }
            }catch (HttpInvocationException e) {
                logger.log(Level.INFO, e.getMessage());
                recordFailure();
            }finally {
                updateStatus();
                Invocation newInvo = createInvocation(invocation, method, data);
                if (_status == Status.OPEN) startObserving(newInvo, method, data);
            }
            logger.log(Level.INFO, "End...Monitoring");//REMOVED
        });
    }

    public Date getLastFailureTime(){
        if (lastFailureTime == null) return null;
        return new Date(lastFailureTime);
    }
}
