package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.Entity;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

public interface HttpInteractor<P extends Response, C extends Entity> extends ConfigurableInteractor<P,C> {

    P get(C consume, QueryParam... params) throws HttpInvocationException;
    void get(C consume, List<QueryParam> query, Consumer<P> consumer);

    P post(C consume, String... paths) throws HttpInvocationException;
    void post(C consume, List<String> paths, Consumer<P> consumer);

    P put(C consume, String... paths) throws HttpInvocationException;
    void put(C consume, List<String> paths, Consumer<P> consumer);

    boolean delete(C consume, QueryParam... params) throws HttpInvocationException;
    void delete(C consume, List<QueryParam> query, Consumer<P> consumer);

    <T extends Object> URI getUri(T... params);

    static String authorizationKey() {return "Authorization";}
    static String authorizationValue(String token){return HttpInteractor.prefix() + HttpInteractor.parseToken(token);}
    static String prefix(){return "Bearer ";}
    static String parseToken(String token){
        final String prefix = HttpInteractor.prefix();
        if (token.trim().startsWith(prefix)){
            String pToken = token.trim();
            return pToken.substring(prefix.length());
        }
        return token;
    }
}
