package com.infoworks.lab.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class EventQueue extends AbstractTaskQueue {

    private final TaskQueue exeQueue;
    private final TaskQueue abortQueue;
    private final EventQueueListener handler;

    public EventQueue(int numberOfThreads) {
        numberOfThreads = numberOfThreads <= 0
                ? (Runtime.getRuntime().availableProcessors() / 2)
                : numberOfThreads;
        this.exeQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(numberOfThreads));
        this.abortQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(numberOfThreads));
        this.handler = new EventQueueManager(this);
    }

    public EventQueue() {
        this(1);
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> biConsumer) {
        /*super.onTaskComplete(biConsumer);*/
        exeQueue.onTaskComplete(biConsumer);
    }

    @Override
    public void abort(Task task, Message error) {
        abortQueue.add(task);
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        JmsMessage jmsMessage = convert(task, error);
        handler.abortListener(jmsMessage.toString());
        //
    }

    @Override
    public TaskQueue add(Task task) {
        exeQueue.add(task);
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        JmsMessage jmsMessage = convert(task);
        handler.startListener(jmsMessage.toString());
        //
        return this;
    }

    @Override
    public TaskQueue cancel(Task task) {
        exeQueue.cancel(task);
        return this;
    }
}
