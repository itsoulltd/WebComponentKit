package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public interface Invocation<R extends AutoCloseable, M extends Object> {

    /**
     * In Jersey 2:
     * Connect timeout interval, in milliseconds. The value MUST be an instance convertible to Integer.
     * A value of zero (0) is equivalent to an interval of infinity.
     * The default value is infinity (0).
     *
     * In OkHttp 3:
     * We can specify as we wish.
     */
    enum TIMEOUT{
        CONNECT("CONNECT_TIMEOUT", 10000, TimeUnit.MILLISECONDS),
        READ("READ_TIMEOUT", 10000, TimeUnit.MILLISECONDS),
        WRITE("WRITE_TIMEOUT", 10000, TimeUnit.MILLISECONDS);

        private String key;
        private long value;
        private TimeUnit unit;

        TIMEOUT(String key, long value, TimeUnit unit) {
            this.key = key;
            this.value = value;
            this.unit = unit;
        }

        public String key(){
            return this.key;
        }

        public long value(){
            return this.value;
        }

        public TimeUnit unit(){
            return this.unit;
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
