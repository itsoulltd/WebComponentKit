package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.task.AbortTask;
import com.infoworks.lab.beans.task.SimpleTask;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void stackTest(){

        boolean isSynch = true;
        TaskQueue queue = TaskQueue.createSync(isSynch);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);
        //
        queue.onTaskComplete((result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            if (counter.decrementAndGet() == 0)
                latch.countDown();
        });
        //
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        counter.incrementAndGet();

        queue.add(new SimpleTask("Hello bro! I am Hayes"));
        counter.incrementAndGet();

        queue.add(new SimpleTask("Hi there! I am Cris"));
        counter.incrementAndGet();

        queue.add(new SimpleTask("Let's bro! I am James"));
        counter.incrementAndGet();
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void stackAbortTest(){

        boolean isSynch = false;
        TaskQueue queue = TaskQueue.createSync(isSynch);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);
        //
        queue.onTaskComplete((result, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(result.toString());
            if (counter.decrementAndGet() == 0)
                latch.countDown();
        });
        //
        queue.add(new SimpleTask("Wow bro! I am Adams"));
        counter.incrementAndGet();

        queue.add(new AbortTask("Hello bro! I am Hayes"));
        counter.incrementAndGet();

        queue.add(new SimpleTask("Hi there! I am Cris"));
        counter.incrementAndGet();

        queue.add(new SimpleTask("Let's bro! I am James"));
        counter.incrementAndGet();
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}