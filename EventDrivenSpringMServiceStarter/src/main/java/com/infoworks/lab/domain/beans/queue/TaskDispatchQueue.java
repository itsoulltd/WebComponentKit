package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.util.function.BiConsumer;

@Component("taskDispatchQueue")
public class TaskDispatchQueue implements TaskQueue, QueuedTaskLifecycleListener {

    private BiConsumer<Message, TaskStack.State> callback;
    private TaskCompletionListener listener;

    @Autowired
    @Qualifier("exeQueue")
    private Queue exeQueue;

    @Autowired
    @Qualifier("abortQueue")
    private Queue abortQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public TaskQueue add(Task task) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = new JmsMessage()
                .setTaskClassName(task.getClass().getName())
                .setMessageClassName(Message.class.getName());
        if (task.getMessage() != null) {
            jmsMessage.setMessageClassName(task.getMessage().getClass().getName())
                    .setPayload(task.getMessage().toString());
        }
        jmsTemplate.convertAndSend(exeQueue, jmsMessage.toString());
        return this;
    }

    @Override
    public void abort(Task task) {
        //Defined:JmsMessage Protocol
        JmsMessage jmsMessage = new JmsMessage()
                .setTaskClassName(task.getClass().getName())
                .setMessageClassName(Message.class.getName());
        if (task.getMessage() != null) {
            jmsMessage.setMessageClassName(task.getMessage().getClass().getName())
                    .setPayload(task.getMessage().toString());
        }
        jmsTemplate.convertAndSend(abortQueue, jmsMessage.toString());
    }

    @Override
    public TaskQueue cancel(Task task) {
        //TODO:
        return this;
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> biConsumer) {
        this.callback = biConsumer;
    }

    @Override
    public void onTaskComplete(TaskCompletionListener taskCompletionListener) {
        this.listener = taskCompletionListener;
    }

    @Override
    public void failed(Message message) {
        if (callback != null){
            callback.accept(message, TaskStack.State.Failed);
        }else if (listener != null){
            listener.failed(message);
        }
    }

    @Override
    public void finished(Message message) {
        if (callback != null){
            callback.accept(message, TaskStack.State.Finished);
        }else if (listener != null){
            listener.finished(message);
        }
    }

}
