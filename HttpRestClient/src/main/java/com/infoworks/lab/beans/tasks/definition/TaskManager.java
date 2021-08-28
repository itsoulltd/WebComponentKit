package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.AsyncQueueManager;
import com.infoworks.lab.beans.tasks.impl.AsyncTaskManager;
import com.infoworks.lab.beans.tasks.impl.SyncQueueManager;
import com.infoworks.lab.beans.tasks.impl.SyncTaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.TimeUnit;

public interface TaskManager extends AutoCloseable{

    enum State{
        Forward,
        Backward
    }

    static TaskManager createAsync(TaskLifecycleListener listener){
        return new AsyncTaskManager(listener);
    }

    static TaskManager createSync(TaskLifecycleListener listener){
        return new SyncTaskManager(listener);
    }

    static TaskManager createAsyncQ(QueuedTaskLifecycleListener listener){
        return new AsyncQueueManager(listener);
    }

    static TaskManager createSyncQ(QueuedTaskLifecycleListener listener){
        return new SyncQueueManager(listener);
    }

    void start(Task task, Message message);
    void stop(Task task, Message reason);
    void terminateRunningTasks(long timeout, TimeUnit unit);
}
