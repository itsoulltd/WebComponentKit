package com.infoworks.lab.beans.queue.event;

import com.infoworks.lab.beans.queue.AbstractTaskQueue;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class EventQueue extends AbstractTaskQueue {

    private final TaskQueue exeQueue;
    private final TaskQueue abortQueue;
    private final TaskManager taskManager;

    public EventQueue(int numberOfThreads) {
        numberOfThreads = numberOfThreads <= 0
                ? (Runtime.getRuntime().availableProcessors() / 2)
                : numberOfThreads;
        this.exeQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(numberOfThreads));
        this.abortQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(numberOfThreads));
        this.taskManager = new EventQueueManager(this);
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
    }

    @Override
    public TaskQueue add(Task task) {
        exeQueue.add(task);
        return this;
    }

    @Override
    public TaskQueue cancel(Task task) {
        exeQueue.cancel(task);
        return this;
    }
}
