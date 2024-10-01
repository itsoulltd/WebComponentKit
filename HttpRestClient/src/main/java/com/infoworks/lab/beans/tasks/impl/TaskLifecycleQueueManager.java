package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.events.EventType;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class TaskLifecycleQueueManager extends AbstractQueueManager implements QueuedTaskLifecycleListener {

    private TaskStack.State state = TaskStack.State.None;
    private BiConsumer<Message, TaskStack.State> callback;
    private TaskCompletionListener listener;
    private final Queue<Task> beanQueue;
    private final Queue<Task> abortQueue;

    protected ExecutorService service;

    public TaskLifecycleQueueManager(ExecutorService service) {
        this(new ConcurrentLinkedDeque<>(), new ConcurrentLinkedDeque<>(), service);
    }

    public TaskLifecycleQueueManager(Queue<Task> beanQueue, Queue<Task> abortQueue, ExecutorService service) {
        this.beanQueue = beanQueue;
        this.abortQueue = abortQueue;
        this.service = service;
    }

    protected ExecutorService getService() {
        if (service == null){
            synchronized (this){
                service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2 + 1);
            }
        }
        return service;
    }

    @Override
    public void start(Task task, Message message) {
        //getService().execute(() -> super.start(task, message));
        //New:
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Forward);
            //Call Execute:
            boolean mustAbort = false;
            Future<Message> futureMsg = getService().submit(() -> {
                Message msg = new Message();
                try {
                    msg = task.execute(message);
                } catch (RuntimeException e) {
                    msg.setEvent(new Event().setEventType(EventType.ERROR));
                    msg.setPayload(String.format("{\"error\":\"%s\", \"status\":500}", e.getMessage()));
                }
                return msg;
            });
            //End Execute:
            Message msg = null;
            try {
                msg = futureMsg.get();
                if (msg != null && msg.getEvent() != null) {
                    mustAbort = msg.getEvent().getEventType() == EventType.ERROR;
                }
            } catch (InterruptedException | ExecutionException e) {
                mustAbort = true;
            }
            //
            if (getListener() != null) {
                if (mustAbort) {
                    getListener().abort(task, msg);
                } else {
                    getListener().after(task, State.Forward);
                    getListener().finished(msg);
                }
            }
        }
    }

    @Override
    public void stop(Task task, Message reason) {
        //getService().execute(()-> super.stop(task, reason));
        //New:
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Backward);
            //Call Execute:
            Future<Message> future = getService().submit(() -> {
                Message msg = new Message();
                try {
                    msg = task.abort(reason);
                } catch (RuntimeException e) {
                    msg.setEvent(new Event().setEventType(EventType.ERROR));
                    msg.setPayload(String.format("{\"error\":\"%s\", \"status\":500}", e.getMessage()));
                }
                return msg;
            });
            //End Execute:
            Message msg = null;
            try {
                msg = future.get();
            } catch (InterruptedException | ExecutionException e) {}
            //
            if (getListener() != null) {
                getListener().after(task, State.Backward);
                getListener().failed(msg);
            }
        }
    }

    public TaskStack.State getState() {
        return state;
    }

    @Override
    public void before(Task task, TaskManager.State state) {
        if (state == TaskManager.State.Forward){
            beanQueue.poll();
        }else if (state == TaskManager.State.Backward){
            abortQueue.poll();
        }
    }

    @Override
    public void after(Task task, TaskManager.State state) {
        if (!beanQueue.isEmpty()){
            Task cTask = beanQueue.peek();
            start(cTask, null);
        }
        if (!abortQueue.isEmpty()){
            Task cTask = abortQueue.peek();
            stop(cTask, null);
        }
        //
        if (beanQueue.isEmpty() && abortQueue.isEmpty()){
            synchronized (this){
                this.state = TaskStack.State.None;
            }
        }
    }

    @Override
    public void abort(Task task, Message error) {
        boolean isFirstTask = abortQueue.isEmpty();
        abortQueue.add(task);
        if (isFirstTask){
            stop(task, error);
        }
    }

    @Override
    public void failed(Message reason) {
        try {
            if (callback != null){
                callback.accept(reason, TaskStack.State.Failed);
            }else if (listener != null){
                listener.failed(reason);
            }
        } catch (Exception e) {}
    }

    @Override
    public void finished(Message result) {
        try {
            if (callback != null){
                callback.accept(result, TaskStack.State.Finished);
            }else if (listener != null){
                listener.finished(result);
            }
        } catch (Exception e) {}
    }

    @Override
    public QueuedTaskLifecycleListener getListener() {
        return this;
    }

    @Override
    public void setListener(QueuedTaskLifecycleListener listener) {
        //Pass
    }

    public void setOnCompleteCallback(BiConsumer<Message, TaskStack.State> callback) {
        this.callback = callback;
    }

    public void setOnCompleteListener(TaskCompletionListener listener) {
        this.listener = listener;
    }

    @Override
    public void terminateRunningTasks(long timeout, TimeUnit unit) {
        try {
            if (!getService().isShutdown()){
                if (timeout <= 0l)
                    getService().shutdownNow();
                else {
                    getService().shutdown();
                    getService().awaitTermination(timeout, unit);
                }
            }
        } catch (Exception e) {}
        finally {
            service = null;
        }
    }

    @Override
    public void close() throws Exception {
        terminateRunningTasks(0l, TimeUnit.SECONDS);
        this.listener = null;
    }
}
