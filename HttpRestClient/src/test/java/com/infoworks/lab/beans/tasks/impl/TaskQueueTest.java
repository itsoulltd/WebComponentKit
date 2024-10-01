package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.queue.event.EventQueue;
import com.infoworks.lab.beans.queue.fakejms.JMSQueue;
import com.infoworks.lab.beans.task.AbortTask;
import com.infoworks.lab.beans.task.ExampleTask;
import com.infoworks.lab.beans.task.SimpleTask;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.rest.models.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TaskQueueTest {

    private TaskQueue queue;

    @Before
    public void before(){
        queue = TaskQueue.createSync(false);
    }

    @After
    public void after(){
        queue = null;
    }

    @Test
    public void queueTest(){
        //Initialize:
        TaskQueue queue = TaskQueue.createSync(true);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        queue.onTaskComplete((result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        queue.add(new SimpleTask("Hello bro! I am Hayes"));
        queue.add(new SimpleTask("Hi there! I am Cris"));
        queue.add(new SimpleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void queueConcurrentTest(){
        //Initialize:
        TaskQueue queue = TaskQueue.createAsync(Executors.newFixedThreadPool(3));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        queue.onTaskComplete((result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        queue.add(new SimpleTask("Hello bro! I am Hayes"));
        queue.add(new SimpleTask("Hi there! I am Cris"));
        queue.add(new SimpleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void executorConcurrencyTest(){
        //Initialize:
        ExecutorService queue = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        Consumer<Message> callback = (msg) -> {
            System.out.println(msg.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        };
        //
        queue.submit(() -> {
            new SimpleTask(callback).execute(new Message().setPayload("Wow bro! I am Adams"));
        });
        queue.submit(() -> {
            new SimpleTask(callback).execute(new Message().setPayload("Hello bro! I am Hayes"));
        });
        queue.submit(() -> {
            new SimpleTask(callback).execute(new Message().setPayload("Hi there! I am Cris"));
        });
        queue.submit(() -> {
            new SimpleTask(callback).execute(new Message().setPayload("Let's bro! I am James"));
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void queueAbortTest(){
        //Initialize:
        boolean isSync = true;
        TaskQueue queue = TaskQueue.createSync(isSync);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        queue.onTaskComplete((result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        queue.add(new AbortTask("Hello bro! I am Hayes"));
        queue.add(new SimpleTask("Hi there! I am Cris"));
        queue.add(new SimpleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void queueAbortTestV2(){
        //Initialize:
        TaskQueue queue = TaskQueue.createAsync(Executors.newFixedThreadPool(2));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(6);
        //
        queue.onTaskComplete((result, state) -> {
            try {
                System.out.println("State: " + state.name());
                System.out.println(result.toString());
            } catch (Exception e) {}
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        queue.add(new AbortTask("01"));
        queue.add(new AbortTask("02"));
        queue.add(new AbortTask("03"));
        queue.add(new AbortTask("04"));
        queue.add(new AbortTask("05"));
        queue.add(new AbortTask("06"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void eventQueueTest() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        EventQueue queue = new EventQueue();
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //Adding Into Queue:
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        queue.add(new SimpleTask("Hello bro! I am Hayes"));
        queue.add(new SimpleTask("Hi there! I am Cris"));
        queue.add(new SimpleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void jmsQueueTest() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        JMSQueue queue = new JMSQueue();
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //Adding Into Queue:
        queue.add(new ExampleTask("Wow bro! I am Adams"));
        queue.add(new ExampleTask("Hello bro! I am Hayes"));
        queue.add(new ExampleTask("Hi there! I am Cris"));
        queue.add(new ExampleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}