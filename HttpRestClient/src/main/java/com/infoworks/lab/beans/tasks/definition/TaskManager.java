package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.AsyncTaskManager;
import com.infoworks.lab.beans.tasks.impl.SyncTaskManager;
import com.infoworks.lab.beans.tasks.impl.TaskLifecycleQueueManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface TaskManager extends AutoCloseable{

    enum State{
        Forward,
        Backward
    }

    static TaskManager createSync(TaskLifecycleListener listener){
        return new SyncTaskManager(listener);
    }

    static TaskManager createAsync(TaskLifecycleListener listener){
        return new AsyncTaskManager(listener);
    }

    static TaskManager createAsync(TaskLifecycleListener listener, ExecutorService service){
        return new AsyncTaskManager(listener, service);
    }

    static TaskManager createAsyncQ(QueuedTaskLifecycleListener listener){
        return new TaskLifecycleQueueManager(null);
    }

    static TaskManager createAsyncQ(QueuedTaskLifecycleListener listener, ExecutorService service){
        return new TaskLifecycleQueueManager(service);
    }

    static TaskManager createSyncQ(QueuedTaskLifecycleListener listener){
        return new TaskLifecycleQueueManager(Executors.newSingleThreadExecutor());
    }

    static TaskManager createSyncQ(QueuedTaskLifecycleListener listener, ExecutorService service){
        return new TaskLifecycleQueueManager(service);
    }

    void start(Task task, Message message);
    void stop(Task task, Message reason);
    void terminateRunningTasks(long timeout, TimeUnit unit);
}
