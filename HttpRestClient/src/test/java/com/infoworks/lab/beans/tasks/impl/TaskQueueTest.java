package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.queue.AbstractTaskQueue;
import com.infoworks.lab.beans.queue.AbstractTaskQueueManager;
import com.infoworks.lab.beans.queue.JmsMessage;
import com.infoworks.lab.beans.task.AbortTask;
import com.infoworks.lab.beans.task.SimpleTask;
import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

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
        //Initialize:
        boolean isSynch = true;
        TaskQueue queue = TaskQueue.createSync(isSynch);
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
    public void stackAbortTest(){
        //Initialize:
        boolean isSynch = false;
        TaskQueue queue = TaskQueue.createSync(isSynch);
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
    public void jmsQueueTest() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        JmsQueue queue = new JmsQueue();
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

    /**
     *
     */
    public static class JmsQueue extends AbstractTaskQueue {

        private final TaskQueue exeQueue;
        private final QueueEventListener handler;

        public JmsQueue() {
            this.exeQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(5));
            this.handler = new JmsQueueManager(this);
        }

        @Override
        public void onTaskComplete(BiConsumer<Message, TaskStack.State> biConsumer) {
            /*super.onTaskComplete(biConsumer);*/
            exeQueue.onTaskComplete(biConsumer);
        }

        @Override
        public void abort(Task task, Message error) {
            JmsMessage jmsMessage = convert(task, error);
            //put into abortQueue:
            handler.abortListener(jmsMessage.toString());
        }

        @Override
        public TaskQueue add(Task task) {
            JmsMessage jmsMessage = convert(task);
            exeQueue.add(task);
            handler.startListener(jmsMessage.toString());
            return this;
        }

        @Override
        public TaskQueue cancel(Task task) {
            return this;
        }
    }

    /**
     *
     */
    public static class JmsQueueManager extends AbstractTaskQueueManager implements QueueEventListener {

        public JmsQueueManager(QueuedTaskLifecycleListener listener) {
            super(listener);
        }

        @Override
        protected Task createTask(String text) throws ClassNotFoundException, IOException
                , IllegalAccessException, InstantiationException
                , NoSuchMethodException, InvocationTargetException {
            Task task = super.createTask(text);
            //Inject dependency into Task during MOM's task execution.
            return task;
        }

        @Override
        public void startListener(String msg) {
            handleTextOnStart(msg);
        }

        @Override
        public void abortListener(String msg) {
            handleTextOnStop(msg);
        }
    }

    /**
     * Handle simulation for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka
     */
    public interface QueueEventListener {
        void startListener(String msg);
        void abortListener(String msg);
    }

}