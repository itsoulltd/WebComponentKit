package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import java.util.Random;
import java.util.function.Function;

public class ASimpleTask  implements Task {

    private Task nextTask;
    private Message message;
    private Function<Message, Message> converter;

    public ASimpleTask() {}

    public ASimpleTask(String message) {
        this.message = new Message();
        this.message.setPayload(message);
    }

    public ASimpleTask(String message, Function<Message, Message> converter) {
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

    @Override
    public Message execute(Message message) throws RuntimeException {
        System.out.println("I am Starting...");
        System.out.println("Doing jobs..." + getMessage().getPayload());
        int rand = new Random().nextInt(6) + 1;
        try {
            Thread.sleep(rand * 1000);
            Response response = new Response();
            response.setStatus(200);
            if (message == null || message.getPayload() == null){
                MSGEvent msg = new MSGEvent();
                msg.setStatus(200 + rand);
                msg.setMessage("Working!");
                response.setEvent(msg);
            }else{
                response.setEvent(message.getEvent(MSGEvent.class));
            }
            System.out.println("My Jobs...Done");
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        System.out.println("I am Aborting...");
        System.out.println("Doing revert ...:" + getMessage().getPayload());
        int rand = new Random().nextInt(6) + 1;
        try {
            Thread.sleep(rand * 1000);
            Response response = new Response();
            response.setStatus(500);
            response.setError("Not Sure why! May be Covid-19");
            System.out.println("My revert process...Done");
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
