package com.infoworks.lab.client.spring;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.AbstractTemplate;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Route;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class HttpTemplate<P extends Response, C extends EntityInterface> extends AbstractTemplate implements HttpInteractor<P,C> {

    private String _domain;
    private Class<P> inferredProduce;
    private Class<C> inferredConsume;
    private List<Property> properties = new ArrayList<>();
    private RestTemplate template;

    public HttpTemplate(){
        this(Response.class, Message.class);
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
        if (config != null) {
            Arrays.stream(config).forEach(o -> {
                if (o instanceof URI
                        || o instanceof URL){
                    _domain = o.toString();
                } else if (o instanceof Property) {
                    properties.add((Property) o);
                } else if (o instanceof Class<?>) {
                    if (inferredProduce == null
                            && (Response.class.isAssignableFrom((Class<?>) o)))
                        inferredProduce = (Class<P>) o;
                    else if (inferredConsume == null) inferredConsume = (Class<C>) o;
                }
            });
        }
    }

    protected String domain() throws MalformedURLException {
        if (Objects.nonNull(_domain)) return _domain;
        _domain = String.format("%s%s:%s%s", schema(), host(), port(), validatePaths(api()));
        URL url = new URL(_domain);
        return url.toString();
    }

    protected String routePath() {
        String routeTo = "";
        if (getClass().isAnnotationPresent(Route.class)){
            routeTo = getClass().getAnnotation(Route.class).value();
        }
        return routeTo;
    }

    @SuppressWarnings("Duplicates")
    protected StringBuffer validatePaths(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (String str : params) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.length() > 2 && trimmed.endsWith("/"))
                trimmed = trimmed.substring(0, trimmed.length() - 1);

            if(trimmed.startsWith("/"))
                buffer.append(trimmed);
            else if(trimmed.startsWith("?"))
                buffer.append(trimmed);
            else
                buffer.append("/" + trimmed);
        }
        return buffer;
    }

    private Class<P> getInferredProduce(){
        if (inferredProduce == null) inferredProduce = (Class<P>) Response.class;
        return inferredProduce;
    }

    private Class<? extends EntityInterface> getInferredConsume(){
        if (inferredConsume == null) inferredConsume = (Class<C>) Message.class;
        return inferredConsume;
    }

    public Property[] getProperties(){
        return properties.toArray(new Property[0]);
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

    protected RestTemplate getTemplate() {
        if (this.template == null)
            this.template = new RestTemplate();
        return this.template;
    }

    public void setTemplate(RestTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("Duplicates")
    private HttpHeaders createSecureHeader(EntityInterface consume){
        HttpHeaders headers = new HttpHeaders();
        if (consume != null){
            Map<String, Object> data = consume.marshallingToMap(true);
            Optional<Map.Entry<String, Object>> first = data.entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().contains("authorization")
                            || entry.getKey().toLowerCase().contains("accesstoken")
                            || entry.getKey().toLowerCase().contains("token")
                            || entry.getKey().toLowerCase().contains("bearertoken"))
                    .findFirst();
            if (first.isPresent()) {
                headers.set(HttpInteractor.authorizationKey()
                        , HttpInteractor.authorizationValue(first.get().getValue().toString()));
            }
        }
        return headers;
    }

    @Override
    public P get(C consume, QueryParam... params) throws HttpInvocationException {
        //
        Class<P> type = getInferredProduce();
        try {
            //Prepare headers & body:
            HttpHeaders headers = createSecureHeader(consume);
            HttpEntity<C> entity = new HttpEntity<>(consume, headers);
            //Prepare request-uri:
            String queryParam = urlencodedQueryParam(params);
            String rootUri = resourcePath(queryParam);
            ResponseEntity<String> rs = getTemplate().exchange(rootUri, HttpMethod.GET, entity, String.class);
            List<P> produce = inflateJson(rs.getBody(), type);
            return (produce != null && !produce.isEmpty())
                    ? produce.get(0) : null;
        }catch (Exception e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void get(C consume, List<QueryParam> query, Consumer<P> consumer) {
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

    @Override
    public P post(C consume, String... paths) throws HttpInvocationException {
        //
        Class<P> type = getInferredProduce();
        try {
            //Prepare headers & body:
            HttpHeaders headers = createSecureHeader(consume);
            HttpEntity<C> entity = new HttpEntity<>(consume, headers);
            //Prepare request-uri:
            String rootUri = resourcePath(paths);
            ResponseEntity<String> rs = getTemplate().exchange(rootUri, HttpMethod.POST, entity, String.class);
            List<P> produce = inflateJson(rs.getBody(), type);
            return (produce != null && !produce.isEmpty())
                    ? produce.get(0) : null;
        }catch (Exception e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void post(C consume, List<String> paths, Consumer<P> consumer) {
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

    @Override
    public P put(C consume, String... paths) throws HttpInvocationException {
        //
        Class<P> type = getInferredProduce();
        try {
            //Prepare headers & body:
            HttpHeaders headers = createSecureHeader(consume);
            HttpEntity<C> entity = new HttpEntity<>(consume, headers);
            //Prepare request-uri:
            String rootUri = resourcePath(paths);
            ResponseEntity<String> rs = getTemplate().exchange(rootUri, HttpMethod.PUT, entity, String.class);
            List<P> produce = inflateJson(rs.getBody(), type);
            return (produce != null && !produce.isEmpty())
                    ? produce.get(0) : null;
        }catch (Exception e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void put(C consume, List<String> paths, Consumer<P> consumer) {
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

    @Override
    public boolean delete(C consume, QueryParam... params) throws HttpInvocationException {
        try {
            //Prepare headers & body:
            HttpHeaders headers = createSecureHeader(consume);
            HttpEntity<C> entity = new HttpEntity<>(consume, headers);
            //Prepare request-uri:
            String queryParam = urlencodedQueryParam(params);
            String rootUri = resourcePath(queryParam);
            ResponseEntity<String> response = getTemplate().exchange(rootUri, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCodeValue() == 500) throw new HttpInvocationException("Internal Server Error!");
            return response.getStatusCodeValue() == 200;
        }catch (Exception e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    @Override @SuppressWarnings("Duplicates")
    public void delete(C consume, List<QueryParam> query, Consumer<P> consumer) {
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

    @Override @SuppressWarnings("Duplicates")
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

}
