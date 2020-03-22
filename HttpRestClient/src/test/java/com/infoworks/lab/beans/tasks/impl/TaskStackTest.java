package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class TaskStackTest {

    TaskStack stack = new TransactionStack();

    @Test
    public void stackTest(){

        CountDownLatch latch = new CountDownLatch(1);

        stack.push(new ASimpleTask("Hi there! I am Cris"));
        stack.push(new ASimpleTask("Hello bro! I am Towhid"));
        stack.commit(false, (result) -> {
            System.out.println(result.toString());
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    public static class ASimpleTask implements Task{

        private Task nextTask;
        private Message message;

        public ASimpleTask() {}

        public ASimpleTask(String message) {
            this.message = new Message();
            this.message.setPayload(message);
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
                response.setStatus(201);
                response.setPayload("{\"message\":\"i am fine!\"}");
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
        public Function<Message, Message> converter() {
            return null;
        }
    }

}