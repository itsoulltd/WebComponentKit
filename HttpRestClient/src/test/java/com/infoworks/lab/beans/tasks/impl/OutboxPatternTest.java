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
     * Story:
     * Let’s say a customer places an order and order is inserted into the order database.
     * While sending the message to the “Delivery Service” some exception happens and the message is not sent.
     * The order entry is still in the database though leaving the system in an inconsistent state.
     * *
     * Ideally you would roll back the entry in the orders database since placing the order and sending
     * an event to the delivery service are part of the same transaction.
     * *
     * But how do you implement transaction across two different types of systems :
     * A database and A messaging service.
     * Such a scenario is quite common in the microservices world.
     * *
     * The solution is: Use Transactional Outbox pattern
     * What is Transactional Outbox pattern?
     * *
     * Transactional Outbox pattern mandates that you create an “Outbox” table to keep track of the asynchronous messages.
     * For every asynchronous message, you make an entry in the “Outbox” table.
     * You then perform the database operation and the “Outbox” insert operations as part of the same transaction.
     * *
     * This way if an error happens in any of the two operations the transaction is rolled back.
     * You then pick up the messages from the ‘Outbox’ and deliver it to your messaging system like Apache Kafka.
     * Also once the message is delivered delete the entry from the Outbox so that it is not processed again.
     * *
     * So let’s say you perform two different operations in the below order as part of a single transaction:
     * 1 Database Insert
     * 2 Asynchronous Message (Insert into Outbox table)
     * If step 1 fails anyway exception will be thrown and step 2 won’t happen.
     * If step 1 succeeds and step 2 (insert into outbox table) fails the transaction will be rolled back.
     * *
     * If the order of operations are reversed:
     * 1 Asynchronous Message (Insert into Outbox table)
     * 2 Database Insert
     * Then if step 1 fails similar to the previous case exception will be thrown and step 2 won’t happen.
     * **
     * In our case ,
     * When a customer places an order , we make an entry in Orders database and another entry in Outbox table.
     * *
     * Once the above transaction completes we pick up the messages from the Outbox table and send it the “Delivery Service”.
     * Notice that if some error happens and the “Delivery Service” did not receive the message ,
     * the messaging system like Apache Kafka will automatically retry to deliver the message.
     *
     * That summarizes the Outbox pattern.
     */

    @Before
    public void before(){}

    @After
    public void after(){}

    @Test
    public void stackTest(){
        //Initialize:
        TaskQueue orderQueue = TaskQueue.createAsync(Executors.newFixedThreadPool(5));
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
                //Handle Failed Status:
                System.out.println(result.getPayload());
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
