package com.infoworks.lab.beans.tasks.nuts;

import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.query.models.Property;

import java.util.function.Function;

public abstract class ExecutableTask<In extends Message, Out extends Message> extends AbstractTask<In, Out> {

    public ExecutableTask() {
        super();
    }

    public ExecutableTask(String message) {
        super(message);
    }

    public ExecutableTask(String message, Function<Message, Message> converter) {
        super(message, converter);
    }

    public ExecutableTask(Property... properties) {
        super(properties);
    }

    public ExecutableTask(Property[] properties, Function<Message, Message> converter) {
        super(properties, converter);
    }

    public ExecutableTask(In message) {
        super(message);
    }

    public ExecutableTask(In message, Function<Message, Message> converter) {
        super(message, converter);
    }

    @Override
    public Out abort(In message) throws RuntimeException {
        return (Out) message;
    }

}
