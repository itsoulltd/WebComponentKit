package com.itsoul.lab.application.bank;

import com.infoworks.tasks.queue.TaskQueue;
import com.infoworks.tasks.stack.TaskStack;
import com.infoworks.objects.Message;

import java.util.function.BiConsumer;

public interface TheFixBank extends TheBank{
    TaskQueue getQueue();
    void onTaskComplete(BiConsumer<Message, TaskStack.State> consumer);
}
