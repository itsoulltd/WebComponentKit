package com.infoworks.lab.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class EventQueue extends AbstractTaskQueue {

    private final TaskQueue exeQueue;
    private final EventQueueListener handler;

    public EventQueue(int numberOfThreads) {
        numberOfThreads = numberOfThreads <= 0
                ? (Runtime.getRuntime().availableProcessors() / 2)
                : numberOfThreads;
        this.exeQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(numberOfThreads));
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
        JmsMessage jmsMessage = convert(task, error);
        //THIS IS FOR SIMULATION:
        handler.abortListener(jmsMessage.toString());
        //
    }

    @Override
    public TaskQueue add(Task task) {
        JmsMessage jmsMessage = convert(task);
        exeQueue.add(task);
        //THIS IS FOR SIMULATION:
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
