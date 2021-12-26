package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

import java.util.Date;

public class RegistrationTask extends AbstractTask<Message, Response> {

    public RegistrationTask(String username, String password, String email, String contact, Date dob, Integer age) {
        super(new Property("username", username)
                , new Property("password", password)
                , new Property("email", email)
                , new Property("contact", contact)
                , new Property("dob", dob)
                , new Property("age", age));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        //TODO: DO THE BUSINESS LOGIC TO MAKE A REGISTRATION:
        String username = getPropertyValue("username").toString();
        String password = getPropertyValue("password").toString();
        String email = getPropertyValue("email").toString();
        String contact = getPropertyValue("contact").toString();
        Date dob = new Date(Long.valueOf(getPropertyValue("dob").toString()));
        Integer age = Integer.valueOf(getPropertyValue("age").toString());
        //....
        System.out.println("Registration is Successful for " + username);
        //....
        return new Response().setMessage("").setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError!";
        return new Response().setMessage(reason).setStatus(500);
    }
}
