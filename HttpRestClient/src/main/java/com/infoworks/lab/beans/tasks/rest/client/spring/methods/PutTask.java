package com.infoworks.lab.beans.tasks.rest.client.spring.methods;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;

public class PutTask extends RestTask<Message, Response> {

    public PutTask() {super();}

    public PutTask(String baseUri, String requestUri, Object...params) {
        super(baseUri, requestUri, params);
    }

    public PutTask(String baseUri, String requestUri, Consumer<String> response) {
        super(baseUri, requestUri, response);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        RestTemplate template = getTemplate();
        try {
            ResponseEntity<String> response = template.exchange(getUri()
                    , HttpMethod.PUT
                    , getBody()
                    , String.class
                    , getParams());
            if (getResponseListener() != null)
                getResponseListener().accept(response.getBody());
        } catch (RestClientException e) {
            return new Response().setStatus(500).setError(e.getMessage());
        }
        return new Response().setStatus(200);
    }
}
