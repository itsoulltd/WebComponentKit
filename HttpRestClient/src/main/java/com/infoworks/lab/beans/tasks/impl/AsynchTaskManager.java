package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsynchTaskManager extends SynchTaskManager {

    private ExecutorService service;

    public AsynchTaskManager() {}

    public AsynchTaskManager(TaskLifecycleListener listener) {
        super(listener);
    }

    public ExecutorService getService() {
        if (service == null){
             synchronized (this){
                 service = Executors.newSingleThreadExecutor();
             }
        }
        return service;
    }

    @Override
    public void start(Task task, Message message) {
        getService().submit(() -> super.start(task, message));
    }

    @Override
    public void stop(Task task, Message reason) {
        getService().submit(() -> super.stop(task, reason));
    }

    @Override
    public void terminateRunningTasks(long timeout, TimeUnit unit) {
        if (service == null) return;
        try {
            if (!service.isShutdown()){
                if (timeout <= 0l)
                    service.shutdownNow();
                else {
                    service.shutdown();
                    service.awaitTermination(timeout, unit);
                }
            }
        } catch (Exception e) {}
        finally {
            service = null;
        }
    }

    @Override
    public void close() throws Exception {
        terminateRunningTasks(0l, TimeUnit.SECONDS);
    }
}
