package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.SimpleTaskManager;
import com.infoworks.lab.rest.models.Message;

public interface TaskManager {

    enum State{
        Forward,
        Backward
    }

    static TaskManager create(TaskLifecycleListener listener){
        return new SimpleTaskManager(listener);
    }
    void start(Task task, Message message);
    void stop(Task task, Message reason);
}
