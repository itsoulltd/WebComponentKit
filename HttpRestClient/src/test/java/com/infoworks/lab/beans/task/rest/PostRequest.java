package com.infoworks.lab.beans.task.rest;

import com.infoworks.lab.beans.tasks.nuts.AbstractTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;

public class PostRequest <In extends Message, Out extends Response> extends AbstractTask<In, Out> {

    private HttpInteractor<Out,In> template;

    public void setTemplate(HttpInteractor<Out, In> template) {
        this.template = template;
    }

    public PostRequest() {}

    @Override
    public Out execute(In message) throws RuntimeException {
        return null;
    }

    @Override
    public Out abort(In message) throws RuntimeException {
        return null;
    }
}
