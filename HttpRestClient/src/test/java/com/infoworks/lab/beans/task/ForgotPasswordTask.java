package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class ForgotPasswordTask extends AbstractTask<Message, Response> {

    public ForgotPasswordTask(String email) {
        super(new Property("email", email));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO FORGOT PASSWORD:
        String email = getPropertyValue("email").toString();
        //....
        System.out.println("Forget Pass is Successful for " + email);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
