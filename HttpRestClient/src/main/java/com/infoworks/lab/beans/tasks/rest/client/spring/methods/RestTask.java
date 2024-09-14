package com.infoworks.lab.beans.tasks.rest.client.spring.methods;

import com.infoworks.lab.beans.tasks.rest.client.base.BaseRequest;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Row;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Consumer;

public abstract class RestTask<In extends Message, Out extends Response>
        extends BaseRequest<In, Out> {

    protected String baseUri;
    protected String requestUri;
    protected String token;
    protected HttpEntity body;
    protected Object[] params = new Object[0];
    protected RestTemplate template;
    protected Consumer<String> responseListener;

    public RestTask(String baseUri, String requestUri, Object...params) {
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.params = params;
    }

    public RestTask(String baseUri, String requestUri) {
        this(baseUri, requestUri, new Object[0]);
    }

    public RestTask() {
        this("", "");
    }

    public RestTask(String baseUri, String requestUri, Consumer<String> responseListener) {
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.responseListener = responseListener;
    }

    public RestTask setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public RestTask setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }

    public RestTask setBody(EntityInterface body, String token) {
        Map<String, Object> data = (body != null)
                ? body.marshallingToMap(true)
                : null;
        return setBody(data, token);
    }

    public RestTask setBody(Row row, String token) {
        Map<String, Object> data = (row != null)
                ? row.keyObjectMap()
                : null;
        return setBody(data, token);
    }

    public RestTask setBody(Map<String, Object> data, String token) {
        setToken(token);
        setBody(new HttpEntity(data, createHeaderFrom(this.token)));
        return this;
    }

    public RestTask setBody(SearchQuery query, String token) {
        setToken(token);
        setBody(new HttpEntity(query, createHeaderFrom(this.token)));
        return this;
    }

    public void setBody(HttpEntity body) {
        this.body = body;
    }

    public HttpEntity getBody() {
        if (this.body == null) {
            return new HttpEntity(null, createHeaderFrom(getToken()));
        }
        return this.body;
    }

    public void setToken(String token) {
        //this.token = token;
        this.token = (token == null) ? "" : token;
    }

    public String getToken() {
        return token;
    }

    public RestTask setParams(Object...params) {
        this.params = params;
        return this;
    }

    protected Object[] getParams() {
        return this.params;
    }

    protected RestTemplate getTemplate() {
        if (this.template == null) {
            this.template = new RestTemplate();
        }
        return this.template;
    }

    public RestTask setTemplate(RestTemplate template) {
        this.template = template;
        return this;
    }

    public RestTask addResponseListener(Consumer<String> response) {
        this.responseListener = response;
        return this;
    }

    protected Consumer<String> getResponseListener() {
        return this.responseListener;
    }

    protected String getUri() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.baseUri.endsWith("/")
                ? this.baseUri.substring(0, (this.baseUri.length() - 1))
                : this.baseUri);
        builder.append(this.requestUri.startsWith("/")
                ? this.requestUri
                : "/" + this.requestUri);
        return builder.toString();
    }
}
