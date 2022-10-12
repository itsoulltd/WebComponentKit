package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.task.CreateOrderTask;
import com.infoworks.lab.beans.task.DispatchDeliveryTask;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.rest.models.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
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
                }
            } else {
                //Handle Failed Task (if-needed)
                //System.out.println(result.getPayload());
            }
            if (counter.get() > 1) {
                counter.decrementAndGet();
            } else {
                latch.countDown();
            }
        });
        //
        Random random = new Random();
        //
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee"));

        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));

        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));

        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));
        orderQueue.add(new CreateOrderTask(counter, "Order For Coffee", random.nextBoolean()));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}
