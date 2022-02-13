package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class LoginTask extends AbstractTask<Message, Response> {

    public LoginTask(String username, String password) {
        super(new Property("username", username), new Property("password", password));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO MAKE A LOGIN:
        String username = getPropertyValue("username").toString();
        String password = getPropertyValue("password").toString();
        //...
        String msg = String.format("%s : %s", username, password);
        System.out.println("Login is Successful for " + username);
        //....
        return new Response().setMessage(msg).setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
