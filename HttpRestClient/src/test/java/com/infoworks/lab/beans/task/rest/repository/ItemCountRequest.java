package com.infoworks.lab.beans.task.rest.repository;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.repository.RestRepository;

public class ItemCountRequest extends ExecutableTask<Message, Response> {

    private RestRepository repository;

    public ItemCountRequest() {}

    public ItemCountRequest(RestRepository repository) {
        this.repository = repository;
    }

    public void setRepository(RestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        try {
            ItemCount count = repository.rowCount();
            return new Response().setStatus(200).setMessage(count.toString());
        } catch (Exception e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
    }
}
