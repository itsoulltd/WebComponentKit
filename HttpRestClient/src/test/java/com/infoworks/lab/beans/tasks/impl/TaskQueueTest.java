package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.queue.event.EventQueue;
import com.infoworks.lab.beans.queue.fakejms.JMSQueue;
import com.infoworks.lab.beans.task.AbortTask;
import com.infoworks.lab.beans.task.ExampleTask;
import com.infoworks.lab.beans.task.MessageEvent;
import com.infoworks.lab.beans.task.SimpleTask;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
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
        TaskQueue queue = TaskQueue.createSync(false);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(5);
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
        queue.add(new AbortTask("01"));
        queue.add(new SimpleTask("Hi there! I am Cris"));
        queue.add(new AbortTask("02"));
        queue.add(new SimpleTask("Let's bro! I am James"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void queueAbortTestV3(){
        //Initialize:
        TaskQueue queue = TaskQueue.createAsync(Executors.newFixedThreadPool(3));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(7);
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
        queue.add(new AbortTask("07"));
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

    @Test
    public void timeoutDurationTest() {
        System.out.println("Duration.ofMillis(1000 * 30)");
        Duration duration = Duration.ofMillis(1000 * 30);
        System.out.println("in millis: " + duration.toMillis());
        System.out.println("in seconds: " + duration.toMillis() / 1000);
        System.out.println("in minutes: " + duration.toMinutes());
        System.out.println("in hours: " + duration.toHours());
        //
        System.out.println("Duration.ofMillis(1000 * 60)");
        duration = Duration.ofMillis(1000 * 60);
        System.out.println("in minutes: " + duration.toMinutes());
        //
        System.out.println("Duration.ofMillis(1000 * 60 * 60)");
        duration = Duration.ofMillis(1000 * 60 * 60);
        System.out.println("in hours: " + duration.toHours());
    }

    @Test
    public void timeoutTest() {
        //Initialize:
        TaskQueue queue = TaskQueue.createAsync(Executors.newFixedThreadPool(2));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
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
        queue.add(new NormalTask("01"));
        queue.add(new TimeOutTask("02"));
        queue.add(new NormalTask("03"));
        queue.add(new TimeOutTask("04"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    //////////////////////////////////////////////////////////////////////////////

    private static class TimeOutTask extends  NormalTask{

        public TimeOutTask(String message) {
            super(message);
        }

        @Override
        public Duration getTimeoutDuration() {
            return Duration.ofMillis(2900);
        }

        @Override
        public Message abort(Message message) throws RuntimeException {
            System.out.println("Abort (TimeOutTask): " + (message != null ? message.getPayload() : "Message is Null"));
            return super.abort(message);
        }
    }

    private static class NormalTask extends ExecutableTask {

        public NormalTask(String message) {
            super(message);
        }

        @Override
        public Duration getTimeoutDuration() {
            return Duration.ofMillis(3200);
        }

        @Override
        public Message execute(Message message) throws RuntimeException {
            System.out.println("("+Thread.currentThread().getName()+") Doing jobs..." + getMessage().getPayload());
            Response response = new Response().setStatus(200);
            try {
                Thread.sleep(3 * 1000);
                MessageEvent msg = new MessageEvent();
                msg.setMessage("Work Done! - " + getMessage().getPayload());
                response.setEvent(msg);
            } catch (InterruptedException e) {}
            return response;
        }
    }

    //////////////////////////////////////////////////////////////////////////////

}