package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class CheckUserExistTask extends AbstractTask<Message, Response> {

    public CheckUserExistTask(String username) {
        super(new Property("username", username));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO CHECK AN User Already Exist or Not:
        String username = getPropertyValue("username").toString();
        //...
        String msg = String.format("User exist: %s", username);
        System.out.println("Check User Exist for " + username);
        //....
        return new Response().setMessage(msg).setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError!";
        return new Response().setMessage(reason).setStatus(500);
    }
}
