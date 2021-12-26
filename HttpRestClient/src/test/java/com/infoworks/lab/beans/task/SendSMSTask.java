package com.infoworks.lab.beans.task;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

public class SendSMSTask extends SendEmailTask {

    public SendSMSTask(String sender, String receiver, String body, String templateId) {
        super(sender, receiver, body, templateId);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO SEND SMS:
        String sender = getPropertyValue("sender").toString();
        String receiver = getPropertyValue("receiver").toString();
        String body = getPropertyValue("body").toString();
        String smsTemplateID = getPropertyValue("templateId").toString();
        //....
        System.out.println("SMS Has Sent To " + receiver);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError!";
        return new Response().setMessage(reason).setStatus(500);
    }
}
