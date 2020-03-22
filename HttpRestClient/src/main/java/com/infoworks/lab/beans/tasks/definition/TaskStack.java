package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

import java.util.function.Consumer;

public interface TaskStack {
    TaskStack push(Task task);
    void commit(boolean reverse, Consumer<Message> onComplete);
    void cancel();
}
