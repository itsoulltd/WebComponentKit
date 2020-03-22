package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public class AbortTask extends SimpleTask {

    public AbortTask(String message, Function<Message, Message> converter) {
        super(message, converter);
    }

    public AbortTask(String message) {
        super(message);
    }

    @Override
    public Message execute(Message message) throws RuntimeException {
        throw new RuntimeException("I AM Aborting! Critical Error @ (" + getMessage().getPayload() + ")");
    }
}
