package com.infoworks.lab.beans.task.rest;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.repository.RestRepository;

import java.util.List;

public class FetchRequest<T> extends ExecutableTask<Message, Response> {

    private RestRepository repository;
    private int page;
    private int pageSize;
    private final int delay;

    public FetchRequest(RestRepository repository, int page, int pageSize) {
        this(repository, page, pageSize, 1000); //default 1 sec delay
    }

    public FetchRequest(RestRepository repository, int page, int pageSize, int delay) {
        this.repository = repository;
        this.page = page;
        this.pageSize = pageSize;
        this.delay = delay;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
        }
        //
        try {
            List<T> res = repository.fetch(page, pageSize);
            System.out.println("Items count: " + res.size());
            String json = Message.marshal(res);
            return new Response().setStatus(200).setMessage(json);
        } catch (Exception e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
    }

}
