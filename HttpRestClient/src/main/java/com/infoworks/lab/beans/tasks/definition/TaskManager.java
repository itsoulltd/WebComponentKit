package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.AsynchQueueManager;
import com.infoworks.lab.beans.tasks.impl.AsynchTaskManager;
import com.infoworks.lab.beans.tasks.impl.SynchQueueManager;
import com.infoworks.lab.beans.tasks.impl.SynchTaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.TimeUnit;

public interface TaskManager extends AutoCloseable{

    enum State{
        Forward,
        Backward
    }

    static TaskManager createAsynch(TaskLifecycleListener listener){
        return new AsynchTaskManager(listener);
    }

    static TaskManager createSynch(TaskLifecycleListener listener){
        return new SynchTaskManager(listener);
    }

    static TaskManager createAsynchQ(QueuedTaskLifecycleListener listener){
        return new AsynchQueueManager(listener);
    }

    static TaskManager createSynchQ(QueuedTaskLifecycleListener listener){
        return new SynchQueueManager(listener);
    }

    void start(Task task, Message message);
    void stop(Task task, Message reason);
    void terminateRunningTasks(long timeout, TimeUnit unit);
}
