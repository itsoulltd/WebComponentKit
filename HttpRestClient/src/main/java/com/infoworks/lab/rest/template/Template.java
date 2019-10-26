package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;

public interface Template<RequestBuilder extends Object
        , InvocationBuilder extends Invocation
        , Response extends AutoCloseable
        , MediaType extends Object> extends AutoCloseable{

    RequestBuilder initializeTarget(String... params) throws MalformedURLException;

    RequestBuilder getTarget();
    void setTarget(RequestBuilder target);

    InvocationBuilder getRequest(MediaType type);
    InvocationBuilder getJsonRequest();
    InvocationBuilder getAuthorizedRequest(EntityInterface consume, MediaType type);
    InvocationBuilder getAuthorizedJsonRequest(EntityInterface consume);

    <T extends EntityInterface> T inflate(Response response, Class<T> type) throws IOException, HttpInvocationException;
    void generateThrowable(Response response) throws HttpInvocationException;

    default boolean isSecure(EntityInterface consume){
        return getSecureEntry(consume) != null;
    }

    default Map.Entry<String, Object> getSecureEntry(EntityInterface consume){
        if (consume != null){
            Map<String, Object> data = consume.marshallingToMap(true);
            Optional<Map.Entry<String, Object>> first = data.entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().contains("authorization")
                            || entry.getKey().toLowerCase().contains("accesstoken"))
                    .findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return null;
    }
}
