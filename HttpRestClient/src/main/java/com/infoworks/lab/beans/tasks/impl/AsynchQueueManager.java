package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchQueueManager extends SynchQueueManager{

    public AsynchQueueManager() {}

    public AsynchQueueManager(QueuedTaskLifecycleListener listener) {
        super(listener);
    }

    public ExecutorService getService() {
        if (service == null){
            synchronized (this){
                service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            }
        }
        return service;
    }

}
