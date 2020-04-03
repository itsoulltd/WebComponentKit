package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class TaskStackTest {

    private TaskStack stack;

    @Before
    public void before(){
        stack = TaskStack.createSynch(false);
    }

    @After
    public void after(){
        stack = null;
    }

    @Test
    public void stackTest(){

        CountDownLatch latch = new CountDownLatch(1);
        //
        stack.push(new SimpleTask("Wow bro! I am Adams"));
        stack.push(new SimpleTask("Hello bro! I am Haise", (message) -> {
            MSGEvent event = (MSGEvent) message.getEvent(MSGEvent.class);
            System.out.println(event.toString());
            return message;
        }));
        stack.push(new SimpleTask("Hi there! I am Cris", (message) -> {
            MSGEvent event = (MSGEvent) message.getEvent(MSGEvent.class);
            event.setMessage("Converted Message");
            event.setStatus(201);
            message.setEvent(event);
            return message;
        }));
        stack.push(new SimpleTask("Let's bro! I am James"));
        //
        stack.commit(false, (result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void stackAbortTest(){

        CountDownLatch latch = new CountDownLatch(1);
        //
        stack.push(new SimpleTask("Wow bro! I am Adams"));
        stack.push(new AbortTask("Hello bro! I am Haise"));
        stack.push(new SimpleTask("Hi there! I am Cris"));
        stack.push(new SimpleTask("Let's bro! I am James"));
        //
        stack.commit(false, (result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}