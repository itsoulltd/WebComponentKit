package com.infoworks.lab.client.okhttp;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.breaker.AbstractCircuitBreaker;
import com.infoworks.lab.rest.template.Interactor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Template;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.Response;

public class HttpCircuitBreaker extends AbstractCircuitBreaker<Response> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public HttpCircuitBreaker() {
        super(500, 5, 2500);
    }

    @Override
    protected Integer parseCode(Response response) {
        if (response != null) response.code();
        return super.parseCode(response);
    }

    @Override  @SuppressWarnings("Duplicates")
    protected boolean isAcceptedResponse(Response response) {
        if (response == null) return false;
        switch (response.code()){
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

    @Override @SuppressWarnings("Duplicates")
    protected Response circuitTest(Invocation invocation, Invocation.Method method, EntityInterface consume) throws HttpInvocationException {
        Response response = null;
        switch (method){
            case GET:
                response = (Response) invocation.get();
                break;
            case POST:
                response = (Response) invocation.post(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
            case DELETE:
                response = (Response) invocation.delete(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
            case PUT:
                response = (Response) invocation.put(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
        }
        return response;
    }

    private Template _template;

    @Override @SuppressWarnings("Duplicates")
    protected Invocation createInvocation(Invocation invocation, Invocation.Method method, EntityInterface consume) {
        reLock.lock();
        try {
            try {
                if (_template == null){
                    URI uri = invocation.getUri();
                    _template = Interactor.create(HttpTemplate.class, uri);
                    _template.setTarget(_template.initializeTarget());
                }
                return _template.isSecure(consume) ? _template.getAuthorizedJsonRequest(consume) : _template.getJsonRequest();
            } catch (IllegalAccessException | InstantiationException
                    | MalformedURLException | IllegalStateException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
            return invocation;
        } finally {
            reLock.unlock();
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void close() {
        if (_template != null) {
            reLock.lock();
            try {
                _template.close();
                _template = null;
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
            }finally {
                reLock.unlock();
            }
        }
        super.close();
    }
}
