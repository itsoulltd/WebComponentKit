package com.infoworks.lab.rest.breaker;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.MediaType;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class SimpleWebInvocation implements Invocation<HttpResponse, MediaType> {

    private URI _uri;
    private Integer _timeout = 1500; //milliseconds

    public SimpleWebInvocation(URI uri) {
        this._uri = uri;
    }

    public SimpleWebInvocation(String uri) {
        this._uri = URI.create(uri);
    }

    @Override
    public HttpResponse get() throws HttpInvocationException {
        return performConnection(_uri, Method.GET, _timeout, MediaType.JSON, null);
    }

    @Override
    public <T extends EntityInterface> HttpResponse post(T data, MediaType mediaType) throws HttpInvocationException {
        return performConnection(_uri, Method.POST, _timeout, mediaType, data);
    }

    @Override
    public <T extends EntityInterface> HttpResponse put(T data, MediaType mediaType) throws HttpInvocationException {
        return performConnection(_uri, Method.PUT, _timeout, mediaType, data);
    }

    @Override
    public HttpResponse delete() throws HttpInvocationException {
        return performConnection(_uri, Method.DELETE, _timeout, MediaType.JSON, null);
    }

    @Deprecated
    private Integer performGetConnecting(URI uri, Integer timeout) throws HttpInvocationException {
        if (_uri == null) throw  new HttpInvocationException("URL Must Not Be Null");
        try {
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(Method.GET.name());
            connection.setConnectTimeout(timeout);
            connection.connect();
            int code = connection.getResponseCode();
            connection.disconnect();
            return code;
        } catch (IOException e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    private HttpResponse performConnection(URI uri, Method method, Integer timeout, MediaType type, EntityInterface consume) throws HttpInvocationException{
        if (_uri == null) throw  new HttpInvocationException("URL Must Not Be Null");
        try {
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(timeout);
            connection.setRequestProperty(type.key(), type.value());
            connection.setDoOutput(true);
            if (consume != null){
                try(OutputStream os = connection.getOutputStream()){
                    os.write(consume.toString().getBytes(type.charset()));
                    os.flush();
                }catch (IOException e) {
                    throw new HttpInvocationException(e.getMessage());
                }
            }
            connection.connect();
            HttpResponse response = new HttpResponse(connection.getResponseCode());
            response.setPayload(connection.getInputStream());
            connection.disconnect();
            return response;
        } catch (IOException e) {
            throw new HttpInvocationException(e.getMessage());
        }
    }

    public void setTimeout(Integer timeout) {
        _timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleWebInvocation that = (SimpleWebInvocation) o;
        return Objects.equals(_uri, that._uri) &&
                Objects.equals(_timeout, that._timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_uri, _timeout);
    }

    @Override
    public URI getUri() {
        return _uri;
    }
}
