package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

public class SendEmailTask extends AbstractTask<Message, Response> {

    public SendEmailTask(String sender, String receiver, String body, String templateId) {
        super(new Property("sender", sender)
                , new Property("receiver", receiver)
                , new Property("templateId",templateId)
                , new Property("body", body));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO SEND EMAIL:
        String sender = getPropertyValue("sender").toString();
        String receiver = getPropertyValue("receiver").toString();
        String body = getPropertyValue("body").toString();
        String emailTemplateID = getPropertyValue("templateId").toString();
        //....
        System.out.println("Email Has Sent To " + receiver);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError!";
        return new Response().setMessage(reason).setStatus(500);
    }
}
