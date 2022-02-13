package com.infoworks.lab.beans.task;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

public class SendOTPSmsTask extends SendSMSTask {

    public SendOTPSmsTask(String sender, String receiver, String body, String templateId) {
        super(sender, receiver, body, templateId);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO SEND OTP SMS:
        String sender = getPropertyValue("sender").toString();
        String receiver = getPropertyValue("receiver").toString();
        String body = getPropertyValue("body").toString();
        String otpTemplateID = getPropertyValue("templateId").toString();
        //....
        System.out.println("OTP Has Sent To " + receiver);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
