package com.infoworks.lab.beans.tasks.definition;

public interface TaskLifecycleListener extends TaskCompletionListener{
    void before(Task task, TaskManager.State state);
    void after(Task task, TaskManager.State state);
}
