package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.queue.event.EventQueue;
import com.infoworks.lab.beans.queue.fakejms.JMSQueue;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskInterfaceTest {

    private TaskStack stack;
    private TaskQueue queue;
    private TaskQueue jmsQueue;
    private TaskQueue eventQueue;

    @Before
    public void before(){
        stack = TaskStack.createSync(false);
        queue = TaskQueue.createSync(false);
        jmsQueue = new JMSQueue();
        eventQueue = new EventQueue();
    }

    @After
    public void after(){
        stack = null;
        queue = null;
        jmsQueue = null;
        eventQueue = null;
    }

    @Test
    public void taskSubclassTestInStack() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        //
        stack.push(new BasicTask());
        stack.push(new BasicExecutableTask());
        stack.push(new TaskWithCustomConstructor("James", 29));
        stack.push(new ExeTaskWithCustomConstructor("Sohana", 23));
        stack.commit(true, (message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void taskSubclassTestInQueue() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //Only 4 - Item should be there!
        queue.add(new BasicTask());
        queue.add(new BasicExecutableTask());
        queue.add(new TaskWithCustomConstructor("James", 29));
        queue.add(new ExeTaskWithCustomConstructor("Sohana", 23));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void taskSubclassTestInEventQueue() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(6);
        //
        eventQueue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //Only 6 - Item should be there!
        eventQueue.add(new BasicTask());
        eventQueue.add(new BasicExecutableTask());
        eventQueue.add(new TaskWithCustomConstructor("James", 29));
        eventQueue.add(new ExeTaskWithCustomConstructor("Sohana", 23));
        eventQueue.add(new JMSTaskWithCustomConstructor("James", 29));
        eventQueue.add(new JMSExeTaskWithCustomConstructor("Sohana", 23));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void taskSubclassTestInJMSQueue() {
        //Initialize:
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(4);
        //
        jmsQueue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state.name());
            System.out.println(message.toString());
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //Only 4 - Item should be there!
        jmsQueue.add(new BasicTask());
        jmsQueue.add(new BasicExecutableTask());
        jmsQueue.add(new JMSTaskWithCustomConstructor("James", 29));
        jmsQueue.add(new JMSExeTaskWithCustomConstructor("Sohana", 23));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    ////////////////////////////////////////////////////////////////////////////////

    public static class BasicTask extends AbstractTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            System.out.println(String.format("%s: %s", BasicTask.class.getSimpleName(), "Success!"));
            return new Response().setStatus(200).setMessage("Success!");
        }

        @Override
        public Response abort(Message message) throws RuntimeException {
            System.out.println(String.format("%s: %s", BasicTask.class.getSimpleName(), "Error!"));
            return new Response().setStatus(500).setMessage("Error!");
        }
    }

    public static class BasicExecutableTask extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            System.out.println(String.format("%s: %s", BasicExecutableTask.class.getSimpleName(), "Success!"));
            return new Response().setStatus(200).setMessage("Success!");
        }
    }

    public static class TaskWithCustomConstructor extends AbstractTask<Message, Response> {

        public TaskWithCustomConstructor(String name, int age) {
            super(new Property("name", name), new Property("age", age));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String name = getPropertyValue("name").toString();
            int age = Integer.valueOf(getPropertyValue("age").toString());
            System.out.println(String.format("%s: Success! %s, %s"
                    , TaskWithCustomConstructor.class.getSimpleName(), name, age));
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }

        @Override
        public Response abort(Message message) throws RuntimeException {
            System.out.println(String.format("%s: %s", TaskWithCustomConstructor.class.getSimpleName(), "Error!"));
            return new Response().setStatus(500).setMessage("Error!");
        }
    }

    public static class ExeTaskWithCustomConstructor extends ExecutableTask<Message, Response> {

        public ExeTaskWithCustomConstructor(String name, int age) {
            super(new Property("name", name), new Property("age", age));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String name = getPropertyValue("name").toString();
            int age = Integer.valueOf(getPropertyValue("age").toString());
            System.out.println(String.format("%s: Success! %s, %s"
                    , ExeTaskWithCustomConstructor.class.getSimpleName(), name, age));
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public static class JMSTaskWithCustomConstructor extends AbstractTask<Message, Response> {

        //Must need Zero param constructor in Case of JMSTask
        public JMSTaskWithCustomConstructor() {}

        public JMSTaskWithCustomConstructor(String name, int age) {
            super(new Property("name", name), new Property("age", age));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String name = getPropertyValue("name").toString();
            int age = Integer.valueOf(getPropertyValue("age").toString());
            System.out.println(String.format("%s: Success! %s, %s"
                    , JMSTaskWithCustomConstructor.class.getSimpleName(), name, age));
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }

        @Override
        public Response abort(Message message) throws RuntimeException {
            System.out.println(String.format("%s: %s", JMSTaskWithCustomConstructor.class.getSimpleName(), "Error!"));
            return new Response().setStatus(500).setMessage("Error!");
        }
    }

    public static class JMSExeTaskWithCustomConstructor extends ExecutableTask<Message, Response> {

        //Must need Zero param constructor in Case of JMSTask
        public JMSExeTaskWithCustomConstructor() {}

        public JMSExeTaskWithCustomConstructor(String name, int age) {
            super(new Property("name", name), new Property("age", age));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String name = getPropertyValue("name").toString();
            int age = Integer.valueOf(getPropertyValue("age").toString());
            System.out.println(String.format("%s: Success! %s, %s"
                    , JMSExeTaskWithCustomConstructor.class.getSimpleName(), name, age));
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

}
