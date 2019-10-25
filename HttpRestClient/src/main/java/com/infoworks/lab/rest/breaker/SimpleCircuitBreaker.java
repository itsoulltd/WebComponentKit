package com.infoworks.lab.rest.breaker;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.MediaType;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.Entity;

import java.net.HttpURLConnection;

public class SimpleCircuitBreaker extends AbstractCircuitBreaker<HttpResponse> {

    public SimpleCircuitBreaker(long timeout, Integer failureThreshold, long retryTimePeriod) {
        super(timeout, failureThreshold, retryTimePeriod);
    }

    public SimpleCircuitBreaker() {
        this(300, 5, 4000);
    }

    protected Integer parseCode(HttpResponse response){
        if (response != null) return response.getCode();
        return super.parseCode(response);
    }

    @Override @SuppressWarnings("Duplicates")
    protected boolean isAcceptedResponse(HttpResponse response) {
        if (response == null) return false;
        switch (response.getCode()){
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
            case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
            case HttpURLConnection.HTTP_BAD_GATEWAY:
            case HttpURLConnection.HTTP_UNAVAILABLE:
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
            case HttpURLConnection.HTTP_VERSION:
                return false;
        }
        return true;
    }

    protected HttpResponse circuitTest(Invocation invocation, Invocation.Method method, Entity data) throws HttpInvocationException {
        HttpResponse response = new HttpResponse(HttpURLConnection.HTTP_NOT_FOUND);
        switch (method){
            case GET:
                response = (HttpResponse) invocation.get();
                break;
            case PUT:
                response = (HttpResponse) invocation.put(data, MediaType.JSON);
                break;
            case POST:
                response = (HttpResponse) invocation.post(data, MediaType.JSON);
                break;
            case DELETE:
                response = (HttpResponse) invocation.delete(data, MediaType.JSON);
                break;
        }
        return response;
    }

    @Override
    protected Invocation createInvocation(Invocation invocation, Invocation.Method method, Entity data) {
        return invocation;
    }
}
