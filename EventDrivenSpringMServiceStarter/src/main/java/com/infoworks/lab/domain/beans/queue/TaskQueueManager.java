package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class TaskQueueManager implements TaskManager {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private QueuedTaskLifecycleListener listener;

    public TaskQueueManager(@Autowired QueuedTaskLifecycleListener listener) {
        this.listener = listener;
    }

    @JmsListener(destination = "exeQueue", concurrency = "1-5")
    public void startlistener(javax.jms.Message message) throws JMSException {
        // retrieve the message content
        TextMessage textMessage = (TextMessage) message;
        String text = textMessage.getText();
        logger.log(Level.INFO, "EXE-QUEUE: Message received {0} ", text);
        try {
            //Defined:JmsMessage Protocol
            JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
            Task task = (Task) Class.forName(jmsMessage.getTaskClassName()).newInstance();
            Class<? extends Message> messageClass = (Class<? extends Message>) Class.forName(jmsMessage.getMessageClassName());
            Message taskMessage = Message.unmarshal(messageClass, jmsMessage.getPayload());
            task.setMessage(taskMessage);
            //
            start(task, taskMessage);
            message.acknowledge();
            //
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException e){
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new JMSException(e.getMessage());
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void start(Task task, Message message) {
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Forward);
            //Call Execute:
            boolean mustAbort = false;
            Message msg = null;
            try {
                msg = task.execute(message);
            } catch (RuntimeException e) {
                mustAbort = true;
                msg = new Message();
                msg.setPayload(String.format("{\"error\":\"%s\", \"status\":500}", e.getMessage()));
            }
            //End Execute:
            if (getListener() != null) {
                if (mustAbort) {
                    getListener().abort(task);
                } else {
                    getListener().after(task, State.Forward);
                    getListener().finished(msg);
                }
            }
        }
    }

    @JmsListener(destination = "abortQueue", concurrency = "1-3")
    public void abortListener(javax.jms.Message message) throws JMSException {
        // retrieve the message content
        TextMessage textMessage = (TextMessage) message;
        String text = textMessage.getText();
        logger.log(Level.INFO, "ABORT-QUEUE: Message received {0} ", text);
        try {
            //Defined:JmsMessage Protocol
            JmsMessage jmsMessage = Message.unmarshal(JmsMessage.class, text);
            Task task = (Task) Class.forName(jmsMessage.getTaskClassName()).newInstance();
            Class<? extends Message> messageClass = (Class<? extends Message>) Class.forName(jmsMessage.getMessageClassName());
            Message taskMessage = Message.unmarshal(messageClass, jmsMessage.getPayload());
            task.setMessage(taskMessage);
            //
            stop(task, taskMessage);
            message.acknowledge();
            //
        }catch (RuntimeException | IOException
                | ClassNotFoundException
                | IllegalAccessException | InstantiationException e){
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new JMSException(e.getMessage());
        }
    }

    @Override
    public void stop(Task task, Message message) {
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Backward);
            //Call Execute:
            Message msg = task.abort(message);
            //End Execute:
            if (getListener() != null) {
                getListener().after(task, State.Backward);
                getListener().failed(msg);
            }
        }
    }

    @Override
    public void terminateRunningTasks(long l, TimeUnit timeUnit) {
        //TODO:
        //send termination to jms-template for stopping current processing or abandon all active task from
        // exeQueue:
    }

    @Override
    public void close() throws Exception {
        //TODO:
        //Clean of any resource:
    }

    public QueuedTaskLifecycleListener getListener() {
        return listener;
    }

}
