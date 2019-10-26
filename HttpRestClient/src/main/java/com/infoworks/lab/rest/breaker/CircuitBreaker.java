package com.infoworks.lab.rest.breaker;

import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Template;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.net.MalformedURLException;

public interface CircuitBreaker<T extends AutoCloseable> extends AutoCloseable{

    enum Status{
        OPEN,
        HALF_OPEN,
        CLOSED
    }

    CircuitBreaker.Status online(Invocation invocation, Invocation.Method method, EntityInterface data);
    T call(Invocation invocation, Invocation.Method method, EntityInterface data);

    default T call(Template template, Invocation.Method method, EntityInterface data) throws MalformedURLException {
        if(template.getTarget() == null) template.setTarget(template.initializeTarget());
        Invocation invocation = template.isSecure(data) ? template.getAuthorizedJsonRequest(data) : template.getJsonRequest();
        return call(invocation, method, data);
    }

    static CircuitBreaker create(Class<? extends CircuitBreaker> type) throws IllegalAccessException, InstantiationException {
        if (type == null) {return new SimpleCircuitBreaker();}
        if (CircuitBreaker.class.isAssignableFrom(type) == false) throw new InstantiationException();
        return type.newInstance();
    }

    @Override
    default void close() {
        //
    }
}
