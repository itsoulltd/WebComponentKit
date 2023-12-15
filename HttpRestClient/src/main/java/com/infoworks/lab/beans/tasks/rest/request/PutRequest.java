package com.infoworks.lab.beans.tasks.rest.request;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.it.soul.lab.sql.entity.EntityInterface;

public class PutRequest<P extends Response, C extends EntityInterface> extends ExecutableTask<Message, Response> {

    private HttpInteractor<P, C> template;
    private C consume;
    private String[] paths;

    public PutRequest() {}

    public PutRequest(HttpInteractor<P, C> template, C consume, String... paths) {
        this.template = template;
        this.consume = consume;
        this.paths = paths;
    }

    public void setTemplate(HttpInteractor<P, C> template) {
        this.template = template;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        if (template == null) throw new RuntimeException(GetRequest.class.getName() + " template is null!");
        try {
            P res = template.put(consume, paths);
            return res;
        } catch (Exception e) {
            return new Response().setStatus(500).setMessage(e.getMessage());
        }
    }
}
