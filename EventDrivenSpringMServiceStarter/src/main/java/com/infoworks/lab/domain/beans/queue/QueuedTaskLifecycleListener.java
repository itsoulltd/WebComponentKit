package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

public interface QueuedTaskLifecycleListener extends TaskLifecycleListener {

    void failed(Task task, Message error);

    @Override
    default void before(Task task, TaskManager.State state) {}

    @Override
    default void after(Task task, TaskManager.State state) {}
}
