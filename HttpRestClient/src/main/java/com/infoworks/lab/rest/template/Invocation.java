package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.net.URI;

public interface Invocation<R extends AutoCloseable, M extends Object> {
    enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }
    R get() throws HttpInvocationException;
    <T extends EntityInterface> R post(T data, M mediaType) throws HttpInvocationException;
    <T extends EntityInterface> R put(T data, M mediaType) throws HttpInvocationException;
    R delete() throws HttpInvocationException;
    default  <T extends EntityInterface> R delete(T data, M mediaType) throws HttpInvocationException{
        return delete();
    }
    URI getUri();
}
