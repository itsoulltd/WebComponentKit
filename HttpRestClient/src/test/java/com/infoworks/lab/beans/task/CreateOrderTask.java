package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

import java.util.concurrent.atomic.AtomicInteger;

public class CreateOrderTask extends ExecutableTask<Message, Response> {

    public CreateOrderTask(AtomicInteger counter, String message, boolean nextRandom) {
        super(new Property("message", message)
                , new Property("orderId", counter.incrementAndGet())
                , new Property("nextRandom", nextRandom));
    }

    public CreateOrderTask(AtomicInteger counter, String message) {
        this(counter, message, true);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        String orderId = getPropertyValue("orderId").toString();
        String msg = getPropertyValue("message").toString() + "[" + orderId + "]";
        boolean nextRandom = (getPropertyValue("nextRandom") != null)
                ? Boolean.parseBoolean(getPropertyValue("nextRandom").toString())
                : true;
        //True will be Success, failed other-wise:
        if (nextRandom) {
            System.out.println(msg + "->" + "Commit: Order In DB");
            return new Response().setStatus(200).setMessage(msg);
        }
        else {
            throw new RuntimeException(msg + "->" + "Commit-Failed: Order In DB");
        }
    }
}
