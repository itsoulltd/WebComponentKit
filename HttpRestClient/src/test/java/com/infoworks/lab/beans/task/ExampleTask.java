package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.query.models.Property;

//////////////////////////////Example of a minimal Task///////////////////////////
public class ExampleTask extends AbstractTask<Message, Response> {

    //Either override default constructor:
    public ExampleTask() {super();}
    //OR
    //Provide an custom constructor:
    public ExampleTask(String data) {
        super(new Property("data", data));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        String savedData = getPropertyValue("data").toString();
        //....
        //....
        return new Response().setMessage(savedData).setStatus(200);
    }

    @Override
    public Response abort(Message message) throws RuntimeException {
        String reason = message != null ? message.getPayload() : "UnknownError! @" + this.getClass().getSimpleName();
        return new Response().setMessage(reason).setStatus(500);
    }
}
