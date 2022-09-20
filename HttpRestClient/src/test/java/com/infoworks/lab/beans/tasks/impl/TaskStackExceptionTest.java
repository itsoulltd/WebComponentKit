package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class TaskStackExceptionTest {

    @Test
    public void exceptionFlowTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //
        TaskStack stack = TaskStack.createSync(true);
        stack.push(new ExceptionTaskTypeA());
        stack.push(new ExceptionTaskTypeB());
        stack.push(new ExceptionTaskTypeC());
        stack.push(new ExceptionTaskTypeD());
        stack.push(new ExceptionTaskTypeE());
        stack.commit(true, (message, status) -> {
            if (status == TaskStack.State.Finished){
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }else {
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    /*private abstract static class ExecutableTask<In extends Message, Out extends Message>
                                    extends AbstractTask<In, Out> {
        @Override
        public Out abort(In message) throws RuntimeException {
            System.out.println("Revert: " + getClass().getName());
            return (Out) message;
        }
    }*/

    public static class ExceptionTaskTypeA extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            if(message != null) System.out.println("Received: " + message.toString());
            return new Response().setStatus(200).setMessage(getClass().getName());
        }

    }

    public static class ExceptionTaskTypeB extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            if(message != null) System.out.println("Received: " + message.toString());
            return new Response().setStatus(200).setMessage(getClass().getName());
        }

    }

    public static class ExceptionTaskTypeC extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            if(message != null) System.out.println("Received: " + message.toString());
            return new Response().setStatus(200).setMessage(getClass().getName());
            //throw new RuntimeException("Annoying Exception Happened IN @"+getClass().getName());
        }

    }

    public static class ExceptionTaskTypeD extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            if(message != null) System.out.println("Received: " + message.toString());
            return new Response().setStatus(200).setMessage(getClass().getName());
        }

    }

    public static class ExceptionTaskTypeE extends ExecutableTask<Message, Response> {

        @Override
        public Response execute(Message message) throws RuntimeException {
            if(message != null) System.out.println("Received: " + message.toString());
            //return new Response().setStatus(200).setMessage(getClass().getName());
            throw new RuntimeException("Dirty Exception Happened IN @"+getClass().getName());
        }

        //By Default: we don't have to override abort in case of ExecutableTask's sub-class,
        //the default implementation of abort in ExecutableTask will do as below implementation.
        /*@Override
        public Response abort(Message message) throws RuntimeException {
            Response response = new Response().setStatus(500).setError("Error@" + this.getClass().getName());
            if (message != null) response.unmarshallingFromMap(message.marshallingToMap(true), true);
            return response;
        }*/

    }

}
