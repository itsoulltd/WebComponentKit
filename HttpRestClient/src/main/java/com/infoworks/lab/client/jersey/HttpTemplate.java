package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.breaker.CircuitBreaker;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Route;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.EntityInterface;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HttpTemplate<P extends com.infoworks.lab.rest.models.Response, C extends EntityInterface> extends HttpAbstractTemplate implements HttpInteractor<P,C> {

    private String _domain;
    private Class<P> inferredProduce;
    private Class<C> inferredConsume;

    public HttpTemplate(){
        /*Must needed to create dynamic instance from type*/
        super();
    }

    public HttpTemplate(Object...config){
        try {
            configure(config);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configure(Object... config) throws InstantiationException{
        if (config == null) throw new InstantiationException();
        Arrays.stream(config).forEach(o -> {
            if (o instanceof URI){
                _domain = ((URI)o).toString();
            }else if (o instanceof Class<?>){
                if (inferredProduce == null) inferredProduce = (Class<P>) o;
                else if (inferredConsume == null) inferredConsume = (Class<C>) o;
            }
        });
    }

    private Class<P> getInferredProduce(){
        if (inferredProduce == null) inferredProduce = (Class<P>) com.infoworks.lab.rest.models.Response.class;
        return inferredProduce;
    }

    private Class<? extends EntityInterface> getInferredConsume(){
        if (inferredConsume == null) inferredConsume = (Class<C>) Entity.class;
        return inferredConsume;
    }

    @Override
    protected synchronized String domain() throws MalformedURLException {
        _domain = String.format("%s%s:%s%s", schema(), host(), port(), validatePaths(api()));
        validateURL(_domain);
        return _domain;
    }

    protected String schema(){
        return "http://";
    }

    protected String host(){
        return "localhost";
    }

    protected Integer port(){
        return 8080;
    }

    protected String api(){
        return "";
    }

    protected URL validateURL(String urlStr) throws MalformedURLException{
        return new URL(urlStr);
    }

    protected String routePath() {
        String routeTo = "/";
        if (getClass().isAnnotationPresent(Route.class)){
            routeTo = getClass().getAnnotation(Route.class).value();
        }
        return routeTo;
    }

    public P get(C consume, QueryParam...params) throws HttpInvocationException {
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            target = initializeTarget();
            if (params != null){
                Arrays.stream(params).forEach(param -> {
                    target = getTarget().queryParam(param.getKey(), param.getValue());
                });
            }
            Response response;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume).get();
            }else{
                response = getJsonRequest().get();
            }
            produce = inflate(response, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return produce;
    }

    public P post(C consume, String...paths) throws HttpInvocationException {
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            target = initializeTarget(paths);
            Response response;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .post(consume, MediaType.APPLICATION_JSON_TYPE);
            }else{
                response = getJsonRequest().post(consume, MediaType.APPLICATION_JSON_TYPE);
            }
            produce = inflate(response, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return produce;
    }

    public P put(C consume , String...paths) throws HttpInvocationException {
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            target = initializeTarget(paths);
            Response response;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .put(consume, MediaType.APPLICATION_JSON_TYPE);
            }else{
                response = getJsonRequest().put(consume, MediaType.APPLICATION_JSON_TYPE);
            }
            produce = inflate(response, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return produce;
    }

    public boolean delete(C consume, QueryParam...params) throws HttpInvocationException {
        try {
            target = initializeTarget();
            if (params != null){
                Arrays.stream(params).forEach(param -> {
                    target = getTarget().queryParam(param.getKey(), param.getValue());
                });
            }
            Response response;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .delete(consume, MediaType.APPLICATION_JSON_TYPE);
            }else{
                response = getJsonRequest().delete(consume, MediaType.APPLICATION_JSON_TYPE);
            }
            if (response.getStatusInfo() == Response.Status.INTERNAL_SERVER_ERROR) throw new HttpInvocationException("Internal Server Error!");
            return response.getStatusInfo() == Response.Status.OK;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private WebTarget target;
    @Override
    public WebTarget getTarget() {
        return target;
    }

    @Override
    public void setTarget(WebTarget target) {
        this.target = target;
    }

    public void get(C consume
            , List<QueryParam> query
            , Consumer<P> consumer){

        //Add to Queue
        addConsumer(consumer);
        submit(() ->{
            P produce = null;
            try {
                QueryParam[] items = (query == null)
                        ? new QueryParam[0]
                        : query.toArray(new QueryParam[0]);
                produce = get(consume, items);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
            notify(produce);
        });
    }

    public void delete(C consume
            , List<QueryParam> query
            , Consumer<P> consumer){

        //Add to Queue
        addConsumer(consumer);
        submit(() ->{
            Boolean produce = null;
            try {
                QueryParam[] items = (query == null)
                        ? new QueryParam[0]
                        : query.toArray(new QueryParam[0]);
                produce = delete(consume, items);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
            notify(produce);
        });
    }

    @Override
    public <T> URI getUri(T... params) {
        return getTarget().getUri();
    }

    public void post(C consume
            , List<String> paths
            , Consumer<P> consumer){

        //Add to queue
        addConsumer(consumer);
        submit(() -> {
            P produce = null;
            try {
                String[] items = (paths == null)
                        ? new String[0]
                        : paths.toArray(new String[0]);
                produce = post(consume, items);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
            notify(produce);
        });
    }

    public void put(C consume
            , List<String> paths
            , Consumer<P> consumer){

        //Add to queue
        addConsumer(consumer);
        submit(() -> {
            P produce = null;
            try {
                String[] items = (paths == null)
                        ? new String[0]
                        : paths.toArray(new String[0]);
                produce = put(consume, items);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
            notify(produce);
        });
    }

    protected <T extends Object> Response execute(EntityInterface consume, Invocation.Method method, T...params) throws MalformedURLException, HttpInvocationException {
        if (params != null){
            if (params instanceof String[]){
                setTarget(initializeTarget((String[]) params));
            }else if (params instanceof QueryParam[]){
                setTarget(initializeTarget());
                Arrays.stream((QueryParam[])params).forEach(param ->
                    setTarget(getTarget().queryParam(param.getKey(), param.getValue()))
                );
            }
        }else {
            setTarget(initializeTarget());
        }
        return execute(consume, method);
    }

    private Response execute(EntityInterface consume, Invocation.Method method) throws HttpInvocationException {
        //CircuitBreaker CODE:
        Response response = null;
        try {
            CircuitBreaker breaker = CircuitBreaker.create(HttpCircuitBreaker.class);
            Invocation invocation = isSecure(consume) ? getAuthorizedJsonRequest(consume) : getJsonRequest();
            if (breaker != null) response = (Response) breaker.call(invocation, method, consume);
            else response = callForwarding(invocation, method, consume);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        //
        return response;
    }

    @SuppressWarnings("Duplicates")
    protected Response callForwarding(Invocation invocation, Invocation.Method method, EntityInterface data) throws HttpInvocationException {
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

}
