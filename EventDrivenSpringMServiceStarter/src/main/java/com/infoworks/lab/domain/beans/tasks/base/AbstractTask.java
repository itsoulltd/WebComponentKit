package com.infoworks.lab.domain.beans.tasks.base;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public abstract class AbstractTask implements Task {

    private Task nextTask;
    private Message message;
    private Function<Message, Message> converter;

    public AbstractTask() {
        this.message = new Message();
    }

    public AbstractTask(String message) {
        this();
        this.message.setPayload(message);
    }

    public AbstractTask(String message, Function<Message, Message> converter) {
        this(message);
        this.converter = converter;
    }

    public AbstractTask(Property...properties){
        this();
        Row row = new Row();
        row.setProperties(Arrays.asList(properties));
        try {
            this.message.setPayload(Message.marshal(row.keyObjectMap()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AbstractTask(Property[] properties, Function<Message, Message> converter){
        this(properties);
        this.converter = converter;
    }

    public AbstractTask(Message message) {
        this.message = message;
    }

    public AbstractTask(Message message, Function<Message, Message> converter) {
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

    public void setConverter(Function<Message, Message> converter) {
        this.converter = converter;
    }
}
