package com.itsoul.lab.application.bank;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;

import java.util.function.BiConsumer;

public interface TheFixBank extends TheBank{
    TaskQueue getQueue();
    void onTaskComplete(BiConsumer<Message, TaskStack.State> consumer);
}
