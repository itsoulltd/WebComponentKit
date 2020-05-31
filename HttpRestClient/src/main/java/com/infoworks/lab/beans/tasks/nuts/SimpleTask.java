package com.infoworks.lab.beans.tasks.nuts;

import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public class SimpleTask extends AbstractTask {

    private Function<Message, Message> executor;
    private Function<Message, Message> aborter;

    public SimpleTask() {super();}

    public SimpleTask(Function<Message, Message> executor) {
        super(new Message(), null);
        this.executor = executor;
    }

    public SimpleTask(Function<Message, Message> executor
            , Function<Message, Message> aborter) {
        this(executor);
        this.aborter = aborter;
    }

    @Override
    public Message execute(Message message) throws RuntimeException {
        if (executor != null)
            return executor.apply(message);
        return null;
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        if (aborter != null)
            return aborter.apply(message);
        return null;
    }
}
