package com.infoworks.lab.beans.tasks.impl;

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

public class TaskInterfaceTest {

    private TaskStack stack;
    private TaskQueue queue;

    @Before
    public void before(){
        stack = TaskStack.createSync(false);
        queue = TaskQueue.createSync(false);
    }

    @After
    public void after(){
        stack = null;
        queue = null;
    }

    @Test
    public void taskSubclassTestInStack() {
        //
    }

    @Test
    public void taskSubclassTestInQueue() {
        //
    }

    ////////////////////////////////////////////////////////////////////////////////

    public static class BasicTask extends AbstractTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            return new Response().setStatus(200).setMessage("Success!");
        }

        @Override
        public Response abort(Message message) throws RuntimeException {
            return new Response().setStatus(500).setMessage("Error!");
        }
    }

    public static class BasicExecutableTask extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
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
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }

        @Override
        public Response abort(Message message) throws RuntimeException {
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
            return new Response().setStatus(200).setMessage(String.format("Success! %s, %s", name, age));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////

}
