package com.infoworks.lab.beans.queue.event;

import com.infoworks.lab.beans.queue.AbstractTaskQueueManager;
import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class EventQueueManager extends AbstractTaskQueueManager {

    public EventQueueManager(QueuedTaskLifecycleListener listener) {
        super(listener);
    }

    @Override
    protected Task createTask(String text) throws ClassNotFoundException, IOException
            , IllegalAccessException, InstantiationException
            , NoSuchMethodException, InvocationTargetException {
        Task task = super.createTask(text);
        //Inject dependency into Task during MOM's task execution.
        return task;
    }

    @Override
    public void terminateRunningTasks(long delay, TimeUnit timeUnit) {
        super.terminateRunningTasks(delay, timeUnit);
    }
}
