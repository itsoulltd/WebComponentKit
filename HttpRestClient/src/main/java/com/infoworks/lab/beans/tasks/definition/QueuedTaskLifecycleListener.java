package com.infoworks.lab.beans.tasks.definition;

public interface QueuedTaskLifecycleListener extends TaskLifecycleListener {

    void abort(Task task);

    @Override
    default void before(Task task, TaskManager.State state) {}

    @Override
    default void after(Task task, TaskManager.State state) {}
}
