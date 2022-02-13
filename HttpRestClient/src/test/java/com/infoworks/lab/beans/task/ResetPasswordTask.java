package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class ResetPasswordTask extends AbstractTask<Message, Response> {

    public ResetPasswordTask(String token, String oldPass, String newPass) {
        super(new Property("token", token), new Property("oldPass", oldPass), new Property("newPass", newPass));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO RESET PASSWORD:
        String token = getPropertyValue("token").toString();
        String oldPass = getPropertyValue("oldPass").toString();
        String newPass = getPropertyValue("newPass").toString();
        //....
        System.out.println("Reset Pass is Successful for " + token);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
