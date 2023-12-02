package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.breaker.CircuitBreaker;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Route;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpTemplate<P extends com.infoworks.lab.rest.models.Response, C extends EntityInterface> extends HttpAbstractTemplate implements HttpInteractor<P,C> {

    private String _domain;
    private Class<P> inferredProduce;
    private Class<C> inferredConsume;
    private List<Property> properties = new ArrayList<>();

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

    @Override @SuppressWarnings("Duplicates")
    public void configure(Object... config) throws InstantiationException{
        if (config == null) throw new InstantiationException();
        Arrays.stream(config).forEach(o -> {
            if (o instanceof URI
                    || o instanceof URL){
                _domain = o.toString();
            }else if(o instanceof Property) {
                properties.add((Property) o);
            }else if (o instanceof Class<?>){
                if (inferredProduce == null
                        && (com.infoworks.lab.rest.models.Response.class.isAssignableFrom((Class<?>) o)))
                    inferredProduce = (Class<P>) o;
                else if (inferredConsume == null) inferredConsume = (Class<C>) o;
            }
        });
    }

    private Class<P> getInferredProduce(){
        if (inferredProduce == null) inferredProduce = (Class<P>) com.infoworks.lab.rest.models.Response.class;
        return inferredProduce;
    }

    private Class<? extends EntityInterface> getInferredConsume(){
        if (inferredConsume == null) inferredConsume = (Class<C>) Message.class;
        return inferredConsume;
    }

    public Property[] getProperties(){
        return properties.toArray(new Property[0]);
    }

    @Override
    protected synchronized String domain() throws MalformedURLException {
        if (Objects.nonNull(_domain)) return _domain;
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
            //Finding those are intended to be path: e.g. new QueryParam("key", "") OR new QueryParam("key", null)
            if (params != null && params.length > 0) {
                String[] paths = parsePathsFrom(params);
                target = initializeTarget(paths);
                mutateTargetWith(params);
            }else{
                target = initializeTarget();
            }
            //
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

    private String[] parsePathsFrom(QueryParam...params){
        return Arrays.stream(params)
                .filter(query -> query.getValue() == null || query.getValue().isEmpty())
                .map(query -> query.getKey())
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    private void mutateTargetWith(QueryParam...params){
        if (target == null) return;
        Arrays.stream(params)
                .filter(query -> query.getValue() != null && !query.getValue().isEmpty())
                .forEach(query -> target = getTarget().queryParam(query.getKey(), query.getValue()));
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
            //Finding those are intended to be path: e.g. new QueryParam("key", "") OR new QueryParam("key", null)
            if (params != null && params.length > 0) {
                String[] paths = parsePathsFrom(params);
                target = initializeTarget(paths);
                mutateTargetWith(params);
            }else {
                target = initializeTarget();
            }
            //
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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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

    public <T extends Object> Response execute(EntityInterface consume, Invocation.Method method, T...params) throws MalformedURLException, HttpInvocationException {
        if (params != null){
            if (params instanceof String[]){
                setTarget(initializeTarget((String[]) params));
            }else if (params instanceof QueryParam[]){
                setTarget(initializeTarget());
                Arrays.stream((QueryParam[])params).forEach(param ->
                    setTarget(getTarget().queryParam(param.getKey(), param.getValue()))
                );
            }else{
                //Means T...params are arbitrary value e.g. "/path-a", "path-b", QueryParam("offset","0"), QueryParam("limit","10") ... etc
                //First: Separate Paths from mixed array:
                String[] paths = Arrays.stream(params).filter(obj -> obj instanceof String).collect(Collectors.toList()).toArray(new String[0]);
                setTarget(initializeTarget(paths));
                //Then: Separate QueryParam from mixed array:
                QueryParam[] queryParams = Arrays.stream(params).filter(obj -> obj instanceof QueryParam).collect(Collectors.toList()).toArray(new QueryParam[0]);
                Arrays.stream(queryParams).forEach(param ->
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
