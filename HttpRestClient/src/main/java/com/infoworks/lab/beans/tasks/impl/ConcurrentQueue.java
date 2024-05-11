package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public class ConcurrentQueue implements TaskQueue {

    private final TaskLifecycleQueueManager manager;

    public ConcurrentQueue(ExecutorService service) {
        this.manager = new TaskLifecycleQueueManager(service);
    }

    @Override
    public TaskQueue add(Task task) {
        manager.start(task, null);
        return this;
    }

    @Override
    public TaskQueue cancel(Task task) {
        //TODO:
        return this;
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> onComplete) {
        manager.setOnCompleteCallback(onComplete);
    }

    @Override
    public void onTaskComplete(TaskCompletionListener onComplete) {
        manager.setOnCompleteListener(onComplete);
    }
}
