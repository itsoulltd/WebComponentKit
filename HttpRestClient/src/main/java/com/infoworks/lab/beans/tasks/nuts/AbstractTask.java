package com.infoworks.lab.beans.tasks.nuts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractTask<In extends Message, Out extends Message> implements Task<In, Out> {

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

    public AbstractTask(In message) {
        this.message = message;
    }

    public AbstractTask(In message, Function<Message, Message> converter) {
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
    public In getMessage() {
        return (In) message;
    }

    public void setMessage(In message) {
        this.message = message;
    }

    @Override
    public Function<Message, Message> getConverter() {
        return converter;
    }

    public void setConverter(Function<Message, Message> converter) {
        this.converter = converter;
    }

    protected Object getPropertyValue(String key) throws RuntimeException{
        String message = getMessage().getPayload();
        if (message != null && Message.isValidJson(message)){
            if(message.startsWith("[")) {throw new RuntimeException("AbstractTask: JsonArray is not supported.");}
            try {
                Map<String, Object> data = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, message);
                Object obj = data.get(key);
                if (obj == null) throw new IOException("AbstractTask: Invalid Property Access");
                return obj;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("AbstractTask: Invalid Property Access");
    }

    protected void updateProperties(Property...properties) throws RuntimeException {
        String payload = getMessage().getPayload();
        if (Message.isValidJson(payload)){
            if (payload.startsWith("[")) { throw new RuntimeException("AbstractTask: JsonArray is not supported."); }
            try {
                Map<String, Object> old = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, payload);
                for (Property property : properties) {
                    old.put(property.getKey(), property.getValue());
                }
                payload = Message.marshal(old);
                getMessage().setPayload(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("AbstractTask: Invalid Property Access");
    }

}
