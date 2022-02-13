package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class LogoutTask extends AbstractTask<Message, Response> {

    public LogoutTask(String token) {
        super(new Property("token", token));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO LOGOUT:
        String token = getPropertyValue("token").toString();
        //....
        System.out.println("Logout is Successful for " + token);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
