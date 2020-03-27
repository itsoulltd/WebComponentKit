package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

import java.util.function.BiConsumer;

public interface TaskQueue {
    TaskQueue add(Task task);
    TaskQueue cancel(Task task);
    void onTaskComplete(BiConsumer<Message, TaskStack.State> onComplete);
    void onTaskComplete(TaskCompletionListener onComplete);
}
