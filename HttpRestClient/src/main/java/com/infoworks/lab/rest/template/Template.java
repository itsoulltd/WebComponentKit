package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.Entity;

import java.io.IOException;
import java.net.MalformedURLException;

public interface Template<RequestBuilder extends Object
        , InvocationBuilder extends Invocation
        , Response extends AutoCloseable
        , MediaType extends Object> extends AutoCloseable{

    RequestBuilder initializeTarget(String... params) throws MalformedURLException;
    abstract RequestBuilder getTarget();
    abstract void setTarget(RequestBuilder target);

    InvocationBuilder getRequest(MediaType type);
    InvocationBuilder getJsonRequest();
    InvocationBuilder getAuthorizedRequest(Entity consume, MediaType type);
    InvocationBuilder getAuthorizedJsonRequest(Entity consume);
    <T extends Entity> T inflate(Response response, Class<T> type) throws IOException, HttpInvocationException;
    void generateThrowable(Response response) throws HttpInvocationException;
    default boolean isSecure(Entity consume){
        return false;
    }
}
