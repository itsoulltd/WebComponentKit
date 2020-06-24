package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;

import java.net.URI;

public interface Invocation<R extends AutoCloseable, M extends Object> {

    enum TIMEOUT{
        CONNECT("CONNECT_TIMEOUT", 10000),
        READ("READ_TIMEOUT", 10000),
        WRITE("WRITE_TIMEOUT", 10000);

        private String key;
        private long value;

        TIMEOUT(String key, long value) {
            this.key = key;
            this.value = value;
        }

        public String key(){
            return this.key;
        }

        public long value(){
            return this.value;
        }
    }
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
    default Invocation<R, M> addProperties(Property...properties) {return this;}
}
