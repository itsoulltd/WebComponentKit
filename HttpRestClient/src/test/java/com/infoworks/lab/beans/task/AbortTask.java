package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public class AbortTask extends ExecutableTask {

    public AbortTask(String message, Function<Message, Message> converter) {
        super(message, converter);
    }

    public AbortTask(String message) {
        super(message);
    }

    @Override
    public Message execute(Message message) throws RuntimeException {
        System.out.println("AbortTask: execute: (" + Thread.currentThread().getName() + ")");
        throw new RuntimeException("I AM Aborting with critical error@ (" + getMessage().getPayload() + ")");
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        System.out.println("AbortTask: abort: (" + Thread.currentThread().getName() + ")");
        return super.abort(message);
    }
}
