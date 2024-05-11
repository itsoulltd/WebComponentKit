package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class AsyncQueueManager extends SyncQueueManager {

    public AsyncQueueManager() {}

    public AsyncQueueManager(QueuedTaskLifecycleListener listener) {
        super(listener);
    }

    public AsyncQueueManager(QueuedTaskLifecycleListener listener, ExecutorService service) {
        super(listener, service);
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
