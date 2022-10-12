package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class OutboxPatternTest {

    /**
     * Note: This is an Hands On Simulation about Outbox Pattern:
     * How this pattern works in 2 way:
     * 1. Transactional outbox with Polling publisher
     * 2. Transactional outbox with Transaction Log Trailing
     * Ref: https://fullstackdeveloper.guru/2022/05/19/how-to-implement-transactional-outbox-design-pattern-in-spring-boot-microservices/
     */

    @Before
    public void before(){}

    @After
    public void after(){}

    @Test
    public void stackTest(){
        //Initialize:
        TaskQueue orderQueue = TaskQueue.createSync(false, Executors.newFixedThreadPool(5));
        TaskQueue deliveryQueue = TaskQueue.createSync(true); //Sync=true means Executors.newSingleThreadExecutor()
        //
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);
        //
        orderQueue.onTaskComplete((result, state) -> {
            if (state == TaskStack.State.Finished){
                if (result instanceof Response){
                    String message = ((Response) result).getMessage();
                    deliveryQueue.add(new DispatchDeliveryTask(message));
                } else {
                    System.out.println(result.getPayload());
                }
            }
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));

        orderQueue.add(new OrderCreatingFailedTask(counter, "Order For Coffee"));

        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));

        orderQueue.add(new OrderCreatingFailedTask(counter, "Order For Coffee"));
        orderQueue.add(new OrderCreatingFailedTask(counter, "Order For Coffee"));
        orderQueue.add(new OrderCreatingFailedTask(counter, "Order For Coffee"));

        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    public static class CreateOrderTask extends ExecutableTask<Message, Response> {

        public CreateOrderTask(AtomicInteger counter, String message) {
            super(new Property("message", message), new Property("orderId", counter.incrementAndGet()));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String orderId = getPropertyValue("orderId").toString();
            String msg = getPropertyValue("message").toString() + "[" + orderId + "]";
            System.out.println(msg + "->" + "Commit: Order In DB");
            return new Response().setStatus(200).setMessage(msg);
        }
    }

    public static class DispatchDeliveryTask extends ExecutableTask<Message, Response> {

        public DispatchDeliveryTask(String message) {
            super(new Property("message", message));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String msg = getPropertyValue("message").toString();
            System.out.println(msg + "->" + "Dispatch: Order Delivery");
            return new Response().setStatus(200).setMessage(msg);
        }
    }

    public static class OrderCreatingFailedTask extends ExecutableTask<Message, Response> {

        public OrderCreatingFailedTask(AtomicInteger counter, String message) {
            super(new Property("message", message), new Property("orderId", counter.incrementAndGet()));
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            String orderId = getPropertyValue("orderId").toString();
            String msg = getPropertyValue("message").toString() + "[" + orderId + "]";
            System.out.println(msg + "->" + "Commit-Failed: Order In DB");
            throw new RuntimeException(msg);
        }
    }

}
