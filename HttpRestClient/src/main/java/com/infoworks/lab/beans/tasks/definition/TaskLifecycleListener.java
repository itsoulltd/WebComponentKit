package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

public interface TaskLifecycleListener {
    void before(Task task, TaskManager.State state);
    void after(Task task, TaskManager.State state);
    void failed(Message reason);
    void finished(Message result);
}
