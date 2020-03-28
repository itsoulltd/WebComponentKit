package com.infoworks.lab.domain.beans.tasks.base;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public abstract class AbstractTask implements Task {

    private Task nextTask;
    private Message message;
    private Function<Message, Message> converter;

    public AbstractTask() {}

    public AbstractTask(String message) {
        this.message = new Message();
        this.message.setPayload(message);
    }

    public AbstractTask(String message, Function<Message, Message> converter) {
        this(message);
        this.converter = converter;
    }

    @Override
    public Task next() {
        return nextTask;
    }

    @Override
    public void linkedTo(Task task) {
        nextTask = task;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public Function<Message, Message> getConverter() {
        return converter;
    }
}
