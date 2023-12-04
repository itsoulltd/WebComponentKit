package com.infoworks.lab.beans.tasks.rest.repository;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;
import com.it.soul.lab.sql.query.models.Property;

import java.io.IOException;
import java.util.List;

public class SearchRequest<T> extends ExecutableTask<Message, Response> {

    private RestRepository repository;
    private SearchQuery query;
    private int delay = 0;

    public SearchRequest() {}

    public SearchRequest(RestRepository repository, SearchQuery query) {
        this(repository, query, 1000); //default 1 sec delay
    }

    public SearchRequest(RestRepository repository, SearchQuery query, int delay) {
        super(new Property("query", query.toString()), new Property("delay", delay));
        this.repository = repository;
        this.query = query;
        this.delay = delay;
    }

    public void setRepository(RestRepository repository) {
        this.repository = repository;
    }

    private void restore() throws RuntimeException {
        if (query == null) {
            try {
                String queryStr = getPropertyValue("query").toString();
                query = Message.unmarshal(SearchQuery.class, queryStr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            List<T> res = repository.search(query);
            String json = Message.marshal(res);
            return new Response().setStatus(200).setMessage(json);
        } catch (Exception e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
    }

}
