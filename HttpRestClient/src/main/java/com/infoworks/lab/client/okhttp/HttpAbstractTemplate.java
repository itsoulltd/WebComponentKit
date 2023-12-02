package com.infoworks.lab.client.okhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.template.AbstractTemplate;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Template;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class HttpAbstractTemplate extends AbstractTemplate implements Template<Request.Builder
        , Invocation<Response, MediaType>
        , Response, MediaType> {

    public class InvocationBuilder implements Invocation<Response, MediaType> {

        private MediaType mediaType;

        public InvocationBuilder(MediaType mediaType) {
            this.mediaType = mediaType;
        }

        public InvocationBuilder(MediaType mediaType, EntityInterface consume){
            this(mediaType);
            Map.Entry<String, Object> entry = getSecureEntry(consume);
            if (entry != null){
                HttpAbstractTemplate.this.getTarget()
                        .addHeader(HttpInteractor.authorizationKey()
                                , HttpInteractor.authorizationValue(entry.getValue().toString()));
            }
        }

        public Response get() throws HttpInvocationException {
            try {
                Request request = HttpAbstractTemplate.this.getTarget()
                        .get().build();
                setUri(request);
                Response response = getWebClient().newCall(request).execute();
                return response;
            } catch (IOException e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        public <T extends EntityInterface> Response post(T data, MediaType mediaType) throws HttpInvocationException {
            try {
                RequestBody requestBody = getRequestBody(data, mediaType);
                Request request = HttpAbstractTemplate.this.getTarget()
                        .post(requestBody)
                        .build();
                setUri(request);
                Response response = getWebClient().newCall(request).execute();
                return response;
            } catch (IOException e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public <T extends EntityInterface> Response put(T data, MediaType mediaType) throws HttpInvocationException {
            try {
                RequestBody requestBody = getRequestBody(data, mediaType);
                Request request = HttpAbstractTemplate.this.getTarget()
                        .put(requestBody)
                        .build();
                setUri(request);
                Response response = getWebClient().newCall(request).execute();
                return response;
            } catch (IOException e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public Response delete() throws HttpInvocationException {
            try {
                Request request = HttpAbstractTemplate.this.getTarget()
                        .delete().build();
                setUri(request);
                Response response = getWebClient().newCall(request).execute();
                return response;
            } catch (IOException e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public <T extends EntityInterface> Response delete(T data, MediaType mediaType) throws HttpInvocationException {
            try {
                RequestBody requestBody = getRequestBody(data, mediaType);
                Request request = HttpAbstractTemplate.this.getTarget()
                        .delete(requestBody)
                        .build();
                setUri(request);
                Response response = getWebClient().newCall(request).execute();
                return response;
            } catch (IOException e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        private <T extends EntityInterface> RequestBody getRequestBody(T data, MediaType mediaType) throws JsonProcessingException {
            ObjectMapper mapper = HttpAbstractTemplate.this.getJsonSerializer();
            String jsonBody = mapper.writeValueAsString(data);
            return RequestBody.create(mediaType, jsonBody);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InvocationBuilder that = (InvocationBuilder) o;
            return Objects.equals(uniquelyIdentifier(), that.uniquelyIdentifier());
        }

        @Override
        public int hashCode() {
            return Objects.hash(uniquelyIdentifier());
        }

        private String _unique;
        private String uniquelyIdentifier(){
            if (_unique == null) {
                synchronized (this){
                    try {
                        _unique = resourcePath();
                    } catch (MalformedURLException e) {
                        _unique = UUID.randomUUID().toString();
                    }
                }
            }
            return _unique;
        }

        private URI _uri;
        @Override
        public URI getUri() {
            return _uri;
        }
        private void setUri(Request request){
            if (request != null)
                _uri = request.url().uri();
        }

    }

    protected String validateDomain() throws MalformedURLException {
        String trimmed = domain().trim();
        if (trimmed.length() > 2 && trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        return trimmed;
    }

    @Override @SuppressWarnings("Duplicates")
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

    private ReentrantLock _lock;
    private OkHttpClient webClient;
    public HttpAbstractTemplate() {_lock = new ReentrantLock();}

    public OkHttpClient getWebClient() {
        return initWebClient(getProperties());
    }

    private OkHttpClient initWebClient(Property...properties){
        if (webClient == null){
            _lock.lock();
            try {
                OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                if (properties != null && properties.length > 0) {
                    for (Property property : properties) {
                        if (property.getKey() != null && property.getValue() != null) {
                            if (Invocation.TIMEOUT.CONNECT.key().equalsIgnoreCase(property.getKey())){
                                builder.connectTimeout(Long.valueOf(property.getValue().toString()), TimeUnit.MILLISECONDS);
                            }
                            if (Invocation.TIMEOUT.READ.key().equalsIgnoreCase(property.getKey())){
                                builder.readTimeout(Long.valueOf(property.getValue().toString()), TimeUnit.MILLISECONDS);
                            }
                            if (Invocation.TIMEOUT.WRITE.key().equalsIgnoreCase(property.getKey())){
                                builder.writeTimeout(Long.valueOf(property.getValue().toString()), TimeUnit.MILLISECONDS);
                            }
                        }
                    }
                }
                webClient = builder.build();
            }catch (Exception e) {}
            finally {
                _lock.unlock();
            }
        }
        return webClient;
    }

    public final Request.Builder initializeTarget(String... params) throws MalformedURLException {
        Request.Builder target = new Request.Builder();
        if (params != null && params.length > 0){
            List<String> listOfPaths = new ArrayList<>();
            listOfPaths.add(HttpAbstractTemplate.this.routePath());
            listOfPaths.addAll(Arrays.asList(params));
            StringBuffer buffer = validatePaths(listOfPaths.toArray(new String[0]));
            target = target.url(HttpAbstractTemplate.this.validateDomain() + buffer.toString());
        }else{
            StringBuffer buffer = validatePaths(HttpAbstractTemplate.this.routePath());
            target = target.url(HttpAbstractTemplate.this.validateDomain() + buffer.toString());
        }
        return target;
    }

    @Override
    public void close() {
        super.close();
        if (webClient != null) {
            try {
                if(webClient.cache() != null) webClient.cache().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract Request.Builder getTarget();
    public abstract void setTarget(Request.Builder target);

    public Invocation<Response, MediaType> getRequest(MediaType type){
        if (getTarget() != null){
                return new InvocationBuilder(type);
        }
        return null;
    }

    public Invocation<Response, MediaType> getJsonRequest(){
        return getRequest(MediaType.parse("application/json;charset=utf-8"));
    }

    public Invocation<Response, MediaType> getAuthorizedRequest(EntityInterface consume, MediaType type){
        if (getTarget() != null){
            return new InvocationBuilder(type, consume);
        }
        return null;
    }

    public Invocation<Response, MediaType> getAuthorizedJsonRequest(EntityInterface consume){
        return getAuthorizedRequest(consume, MediaType.parse("application/json;charset=utf-8"));
    }

    public <T extends EntityInterface> T inflate(Response response, Class<T> type) throws IOException, HttpInvocationException {
        generateThrowable(response);
        String json = response.body().string();
        List items = inflateJson(json, type);
        return (items != null && items.size() > 0) ? (T) items.get(0) : null;
    }

    public void generateThrowable(Response response) throws HttpInvocationException{
        if (response.code() == 500){
            throw new HttpInvocationException("Internal Server Error!");
        }
    }

}
