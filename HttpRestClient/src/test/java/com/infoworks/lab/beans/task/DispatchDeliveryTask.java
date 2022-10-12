package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class DispatchDeliveryTask extends ExecutableTask<Message, Response> {

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
