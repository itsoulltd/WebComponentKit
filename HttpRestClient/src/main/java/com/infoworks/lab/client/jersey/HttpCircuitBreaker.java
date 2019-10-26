package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.breaker.AbstractCircuitBreaker;
import com.infoworks.lab.rest.template.Interactor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Template;
import com.it.soul.lab.sql.entity.EntityInterface;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpCircuitBreaker extends AbstractCircuitBreaker<Response> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public HttpCircuitBreaker() {
        super(500, 5, 2500);
    }

    @Override @SuppressWarnings("Duplicates")
    protected Response circuitTest(Invocation invocation, Invocation.Method method, EntityInterface data) throws HttpInvocationException {
        Response response = null;
        switch (method){
            case GET:
                response = (Response) invocation.get();
                break;
            case POST:
                response = (Response) invocation.post(data, MediaType.APPLICATION_JSON_TYPE);
                break;
            case DELETE:
                response = (Response) invocation.delete(data, MediaType.APPLICATION_JSON_TYPE);
                break;
            case PUT:
                response = (Response) invocation.put(data, MediaType.APPLICATION_JSON_TYPE);
                break;
        }
        return response;
    }

    @Override
    protected Integer parseCode(Response response) {
        if (response == null) return super.parseCode(response);
        return response.getStatus();
    }

    @Override @SuppressWarnings("Duplicates")
    protected boolean isAcceptedResponse(Response response) {
        if (response == null) return false;
        switch (response.getStatus()){
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

    private Template _template;

    @Override
    protected Invocation createInvocation(Invocation invocation, Invocation.Method method, EntityInterface data){
        reLock.lock();
        try {
            try {
                if (_template == null){
                    URI uri = invocation.getUri();
                    _template = Interactor.create(HttpTemplate.class, uri);
                    _template.setTarget(_template.initializeTarget());
                }
                return _template.isSecure(data) ? _template.getAuthorizedJsonRequest(data) : _template.getJsonRequest();
            } catch (IllegalAccessException | InstantiationException
                    | MalformedURLException | IllegalStateException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
            return invocation;
        } finally {
            reLock.unlock();
        }
    }

    @Override
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
