package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import java.util.Random;
import java.util.function.Function;

public class SimpleTask extends AbstractTask {

    private Task nextTask;
    private Message message;
    private Function<Message, Message> converter;

    public SimpleTask() {}

    public SimpleTask(String message) {
        this.message = new Message();
        this.message.setPayload(message);
    }

    public SimpleTask(String message, Function<Message, Message> converter) {
        this.message = new Message();
        this.message.setPayload(message);
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

    private static Random RANDOM = new Random();

    @Override
    public Message execute(Message message) throws RuntimeException {
        System.out.println("("+Thread.currentThread().getName()+") Doing jobs..." + getMessage().getPayload());
        Response response = new Response();
        int rand = RANDOM.nextInt(6) + 1;
        try {
            Thread.sleep(rand * 1000);
            response.setStatus(200);
            if (message == null || message.getPayload() == null){
                MSGEvent msg = new MSGEvent();
                msg.setStatus(200 + rand);
                msg.setMessage("Working!");
                response.setEvent(msg);
            }else{
                response.setEvent(message.getEvent(MSGEvent.class));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        return response;
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        System.out.println("("+Thread.currentThread().getName()+") Doing revert ...:" + getMessage().getPayload());
        Response response = new Response();
        int rand = RANDOM.nextInt(3) + 1;
        try {
            Thread.sleep(rand * 1000);
            response.setStatus(500 + rand);
            if (message == null || message.getPayload() == null){
                response.setError("Not Sure why! May be Covid-19");
            }else {
                response.setEvent(message.getEvent(MSGEvent.class));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        return response;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Function<Message, Message> getConverter() {
        return converter;
    }
}
