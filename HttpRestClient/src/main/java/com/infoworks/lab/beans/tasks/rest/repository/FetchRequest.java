package com.infoworks.lab.beans.tasks.rest.repository;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.repository.RestRepository;
import com.it.soul.lab.sql.query.models.Property;

import java.util.List;

public class FetchRequest<T> extends ExecutableTask<Message, Response> {

    private RestRepository repository;
    private int page;
    private int pageSize;
    private int delay = 0;

    public FetchRequest() {}

    public FetchRequest(RestRepository repository, int page, int pageSize) {
        this(repository, page, pageSize, 1000); //default 1 sec delay
    }

    public FetchRequest(RestRepository repository, int page, int pageSize, int delay) {
        super(new Property("page", page), new Property("pageSize", pageSize), new Property("delay", delay));
        this.repository = repository;
        this.page = page;
        this.pageSize = pageSize;
        this.delay = delay;
    }

    public void setRepository(RestRepository repository) {
        this.repository = repository;
    }

    private void restore() throws RuntimeException {
        if (page <= 0) {
            page = Integer.valueOf(getPropertyValue("page").toString());
        }
        if (pageSize <= 0) {
            pageSize = Integer.valueOf(getPropertyValue("pageSize").toString());
        }
        if (delay <= 0) {
            delay = Integer.valueOf(getPropertyValue("delay").toString());
        }
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        restore();
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
        }
        //
        try {
            List<T> res = repository.fetch(page, pageSize);
            String json = Message.marshal(res);
            return new Response().setStatus(200).setMessage(json);
        } catch (Exception e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
    }

}
