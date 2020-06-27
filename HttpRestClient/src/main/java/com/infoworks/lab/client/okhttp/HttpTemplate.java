package com.infoworks.lab.client.okhttp;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.breaker.CircuitBreaker;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Route;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HttpTemplate<P extends com.infoworks.lab.rest.models.Response, C extends EntityInterface> extends HttpAbstractTemplate implements HttpInteractor<P,C> {

    private Request.Builder target;
    @Override
    public Request.Builder getTarget() {
        return target;
    }

    @Override
    public void setTarget(Request.Builder target) {
        this.target = target;
    }

    private String _domain;
    private Class<P> inferredProduce;
    private Class<C> inferredConsume;
    private List<Property> properties = new ArrayList<>();

    public HttpTemplate(){}

    public HttpTemplate(Object... config){
        try {
            configure(config);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void configure(Object... config) throws InstantiationException {
        if (config == null) throw new InstantiationException();
        for(Object o : config){
            if (o instanceof URI){
                _domain = ((URI)o).toString();
            }else if(o instanceof Property) {
                properties.add((Property) o);
            }else if (o instanceof Class<?>){
                if (inferredProduce == null) inferredProduce = (Class<P>) o;
                else if (inferredConsume == null) inferredConsume = (Class<C>) o;
            }
        }
    }

    private Class<P> getInferredProduce(){
        if (inferredProduce == null) inferredProduce = (Class<P>) com.infoworks.lab.rest.models.Response.class;
        return inferredProduce;
    }

    private Class<? extends EntityInterface> getInferredConsume(){
        if (inferredConsume == null) inferredConsume = (Class<C>) Entity.class;
        return inferredConsume;
    }

    public Property[] getProperties(){
        return properties.toArray(new Property[0]);
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

    protected String urlencodedQueryParam(QueryParam...params){
        if (params == null) return "";
        StringBuffer buffer = new StringBuffer();
        //Separate Paths:
        List<String> pathsBag = new ArrayList<>();
        for (QueryParam query : params) {
            if (query.getValue() != null && !query.getValue().isEmpty()) {
                continue;
            }
            pathsBag.add(query.getKey());
        }
        buffer.append(validatePaths(pathsBag.toArray(new String[0])));
        //Incorporate QueryParams:
        buffer.append("?");
        for (QueryParam query : params){
            if (query.getValue() == null || query.getValue().isEmpty()){
                continue;
            }
            try {
                buffer.append(query.getKey()
                        + "="
                        + URLEncoder.encode(query.getValue(), "UTF-8")
                        + "&");
            } catch (UnsupportedEncodingException e) {}
        }
        String value = buffer.toString();
        value = value.substring(0, value.length()-1);
        return value;
    }

    public P get(C consume , QueryParam...params) throws HttpInvocationException {
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            String queryParam = urlencodedQueryParam(params);
            target = initializeTarget(queryParam);
            Response response = null;
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

    public P post(C consume , String...paths) throws HttpInvocationException {
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            target = initializeTarget(paths);
            Response response = null;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .post(consume, MediaType.parse("application/json;charset=utf-8"));
            }else{
                response = getJsonRequest().post(consume, MediaType.parse("application/json;charset=utf-8"));
            }
            produce = inflate(response, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return produce;
    }

    public P put(C consume , String...paths) throws HttpInvocationException{
        P produce = null;
        Class<P> type = getInferredProduce();
        try {
            target = initializeTarget(paths);
            Response response = null;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .put(consume, MediaType.parse("application/json;charset=utf-8"));
            }else{
                response = getJsonRequest().put(consume, MediaType.parse("application/json;charset=utf-8"));
            }
            produce = inflate(response, type);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return produce;
    }

    public boolean delete(C consume, QueryParam...params) throws HttpInvocationException {
        try {
            String queryParam = urlencodedQueryParam(params);
            target = initializeTarget(queryParam);
            Response response = null;
            if (isSecure(consume)){
                response = getAuthorizedJsonRequest(consume)
                        .delete(consume, MediaType.parse("application/json;charset=utf-8"));
            }else{
                response = getJsonRequest().delete(consume, MediaType.parse("application/json;charset=utf-8"));
            }
            if (response.code() == 500) throw new HttpInvocationException("Internal Server Error!");
            return response.code() == 200;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    public void get(C consume
            , List<QueryParam> query
            , Consumer<P> consumer){

        //Add to Queue
        addConsumer(consumer);
        submit(() -> {
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
        submit(() -> {
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
        try {
            if (params instanceof String[]){
                return URI.create(resourcePath((String[]) params));
            }else if (params instanceof QueryParam[]){
                String queryParam = urlencodedQueryParam((QueryParam[])params);
                return URI.create(resourcePath(queryParam));
            }
        } catch (MalformedURLException e) {}
        return null;
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

    protected <T extends Object> Response execute(EntityInterface consume, Invocation.Method method, T...params) throws MalformedURLException, HttpInvocationException {
        if (params != null){
            if (params instanceof String[]){
                setTarget(initializeTarget((String[]) params));
            }else if (params instanceof QueryParam[]){
                String queryParam = urlencodedQueryParam((QueryParam[]) params);
                setTarget(initializeTarget(queryParam));
            }else{
                //Means T...params are arbitrary value e.g. "/path-a", "path-b", QueryParam("offset","0"), QueryParam("limit","10") ... etc
                //First: Separate Paths from mixed array:
                List<String> paths = new ArrayList<>();
                for (Object query : params) {
                    if (query instanceof String)
                        paths.add((String) query);
                }
                List<String> collector = new ArrayList<>(paths);
                //Then: Separate QueryParam from mixed array:
                List<QueryParam> queryParams = new ArrayList<>();
                for (Object query : params) {
                    if (query instanceof QueryParam)
                        queryParams.add((QueryParam) query);
                }
                String queryParam = urlencodedQueryParam(queryParams.toArray(new QueryParam[0]));
                collector.add(queryParam);
                //Finally:
                setTarget(initializeTarget(collector.toArray(new String[0])));
            }
        }else {
            setTarget(initializeTarget());
        }
        //CircuitBreaker CODE:
        Response response;
        CircuitBreaker breaker = getCircuitBreaker(params);
        Invocation invocation = isSecure(consume) ? getAuthorizedJsonRequest(consume) : getJsonRequest();
        if (breaker != null) response = (Response) breaker.call(invocation, method, consume);
        else response = callForwarding(invocation, method, consume);
        //
        return response;
    }

    protected <T extends Object> CircuitBreaker getCircuitBreaker(T...params){
        //URI uri = getUri(params);//URI.create(resourcePath(params));
        //CircuitBreaker breaker = GeoTrackerDroidKit.shared().getCircuitBreaker(uri, null);
        CircuitBreaker breaker = null;
        try {
            breaker = CircuitBreaker.create(HttpCircuitBreaker.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return breaker;
    }

    @SuppressWarnings("Duplicates")
    protected Response callForwarding(Invocation invocation, Invocation.Method method, EntityInterface consume) throws HttpInvocationException {
        Response response = null;
        switch (method){
            case GET:
                response = (Response) invocation.get();
                break;
            case POST:
                response = (Response) invocation.post(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
            case DELETE:
                response = (Response) invocation.delete(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
            case PUT:
                response = (Response) invocation.put(consume, MediaType.parse("application/json;charset=utf-8"));
                break;
        }
        return response;
    }
}
