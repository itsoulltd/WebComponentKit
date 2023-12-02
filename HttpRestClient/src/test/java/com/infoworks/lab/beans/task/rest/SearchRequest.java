package com.infoworks.lab.beans.task.rest;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;

import java.util.List;

public class SearchRequest<T> extends ExecutableTask<Message, Response> {

    private RestRepository repository;
    private SearchQuery query;
    private final int delay;

    public SearchRequest(RestRepository repository, SearchQuery query) {
        this(repository, query, 1000); //default 1 sec delay
    }

    public SearchRequest(RestRepository repository, SearchQuery query, int delay) {
        this.repository = repository;
        this.query = query;
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
            List<T> res = repository.search(query);
            System.out.println("Search Item count: " + res.size());
            String json = Message.marshal(res);
            return new Response().setStatus(200).setMessage(json);
        } catch (Exception e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
    }

}
