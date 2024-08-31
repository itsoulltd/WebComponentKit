package com.infoworks.lab.beans.task;

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
        System.out.println("AbortTask: execute 01");
        throw new RuntimeException("I AM Aborting! Critical Error @ (" + getMessage().getPayload() + ")");
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        System.out.println("AbortTask: abort 02");
        return super.abort(message);
    }
}
