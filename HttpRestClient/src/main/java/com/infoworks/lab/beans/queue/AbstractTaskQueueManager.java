package com.infoworks.lab.beans.queue;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.impl.AbstractQueueManager;
import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTaskQueueManager extends AbstractQueueManager {

    private QueuedTaskLifecycleListener listener;
    public QueuedTaskLifecycleListener getListener() {
        return listener;
    }
    public void setListener(QueuedTaskLifecycleListener queuedTaskLifecycleListener) {
        this.listener = queuedTaskLifecycleListener;
    }

    public AbstractTaskQueueManager(QueuedTaskLifecycleListener listener) {
        this.listener = listener;
    }

    protected Task createTask(String text)
            throws ClassNotFoundException, IOException, IllegalAccessException
            , InstantiationException, NoSuchMethodException, InvocationTargetException {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
        //Task task = (Task) Class.forName(jmsMessage.getTaskClassName()).newInstance();
        Class taskType = Class.forName(jmsMessage.getTaskClassName());
        Task task = (Task) taskType.getDeclaredConstructor().newInstance();
        //
        Class<? extends Message> messageClass = (Class<? extends Message>) Class.forName(jmsMessage.getMessageClassName());
        Message taskMessage = Message.unmarshal(messageClass, jmsMessage.getPayload());
        task.setMessage(taskMessage);
        return task;
    }

    protected boolean handleTextOnStart(String text) throws RuntimeException {
        try {
            Task task = createTask(text);
            start(task, null);
            return true;
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    protected Message getErrorMessage(String text) throws IOException, ClassNotFoundException {
        JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
        //Handle error-message:
        Class<? extends Message> errorClass = (Class<? extends Message>) Class.forName(jmsMessage.getErrorClassName());
        Message errorMessage = Message.unmarshal(errorClass, jmsMessage.getErrorPayload());
        return errorMessage;
    }

    protected boolean handleTextOnStop(String text) throws RuntimeException {
        try {
            Task task = createTask(text);
            Message errorMessage = getErrorMessage(text);
            stop(task, errorMessage);
            return true;
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Note:
     * Send termination to jms-template for stopping current processing or abandon all active task from
     * e.g. exeQueue, abortQueue, testQueue
     * @param delay
     * @param timeUnit
     */
    @Override
    public void terminateRunningTasks(long delay, TimeUnit timeUnit) {
        /**/
    }

    @Override
    public void close() throws Exception {
        terminateRunningTasks(0l, TimeUnit.SECONDS);
        this.listener = null;
    }

}
