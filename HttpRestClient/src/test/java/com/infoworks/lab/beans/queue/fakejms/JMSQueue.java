package com.infoworks.lab.beans.queue.fakejms;

import com.infoworks.lab.beans.queue.AbstractTaskQueue;
import com.infoworks.lab.beans.queue.JmsMessage;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;

import java.util.function.BiConsumer;

public class JMSQueue extends AbstractTaskQueue {

    private final JMSBrokerTemplate jmsTemplate;

    public JMSQueue(int numberOfThreads) {
        this.jmsTemplate = new JMSBrokerTemplate(this, numberOfThreads);
    }

    public JMSQueue() {
        this(1);
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> biConsumer) {
        super.onTaskComplete(biConsumer);
    }

    @Override
    public TaskQueue add(Task task) {
        JmsMessage jmsMessage = convert(task);
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        jmsTemplate.convertAndSend(jmsMessage.toString());
        return this;
    }

    @Override
    public void abort(Task task, Message error) {
        JmsMessage jmsMessage = convert(task, error);
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        jmsTemplate.send(jmsMessage.toString());
    }

    @Override
    public TaskQueue cancel(Task task) {
        return this;
    }
}
