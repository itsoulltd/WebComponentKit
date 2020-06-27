package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.template.AbstractTemplate;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.infoworks.lab.rest.template.Template;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class HttpAbstractTemplate extends AbstractTemplate implements Template<WebTarget
        , Invocation<Response, MediaType>
        , Response, MediaType> {

    public class InvocationBuilder implements Invocation<Response, MediaType> {

        private MediaType mediaType;
        private javax.ws.rs.client.Invocation.Builder builder;

        public InvocationBuilder(MediaType mediaType) {
            this.mediaType = mediaType;
            this.builder = HttpAbstractTemplate.this.getTarget().request(mediaType);
        }

        public InvocationBuilder(MediaType mediaType, EntityInterface consume){
            this(mediaType);
            Map.Entry<String, Object> entry = getSecureEntry(consume);
            if (entry != null)
                this.builder.header(HttpHeaders.AUTHORIZATION
                        , HttpInteractor.authorizationValue(entry.getValue().toString()));
        }

        @Override
        public Invocation<Response, MediaType> addProperties(Property...properties) {
            if (builder != null && properties != null){
                for (Property property : properties) {
                    if (property.getKey() != null && property.getValue() != null){
                        if (Invocation.TIMEOUT.CONNECT.key().equalsIgnoreCase(property.getKey())) {
                            builder.property(ClientProperties.CONNECT_TIMEOUT, Integer.valueOf(property.getValue().toString()));
                        }
                        if (TIMEOUT.READ.key().equalsIgnoreCase(property.getKey())) {
                            builder.property(ClientProperties.READ_TIMEOUT, Integer.valueOf(property.getValue().toString()));
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public Response get() throws HttpInvocationException {
            try {
                return this.builder.get();
            } catch (Exception e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public <T extends EntityInterface> Response post(T data, MediaType mediaType) throws HttpInvocationException{
            try {
                if (data == null) return this.builder.post(null);
                else return this.builder.post(Entity.entity(data, mediaType));
            } catch (Exception e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public <T extends EntityInterface> Response put(T data, MediaType mediaType) throws HttpInvocationException{
            try {
                if(data == null) return this.builder.put(null);
                else return this.builder.put(Entity.entity(data, mediaType));
            } catch (Exception e) {
                throw new HttpInvocationException(e.getMessage());
            }
        }

        @Override
        public Response delete() throws HttpInvocationException{
            try {
                return this.builder.delete();
            } catch (Exception e) {
                throw new HttpInvocationException(e.getMessage());
            }
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

        @Override
        public URI getUri() {
            return HttpAbstractTemplate.this.getTarget().getUri();
        }
    }


    private final Client webClient;
    public HttpAbstractTemplate() {
        ClientConfig config = new ClientConfig(JacksonJsonProvider.class);
        this.webClient = ClientBuilder.newClient(config);
    }

    public final WebTarget initializeTarget(String... params) throws MalformedURLException {
        WebTarget target = webClient.target(domain());
        String routeTo = routePath();
        target = target.path(routeTo);
        if (params != null && params.length > 0){
            StringBuffer buffer = validatePaths(params);
            target = target.path(buffer.toString());
        }
        return target;
    }

    @Override
    public void close() {
        super.close();
        if (webClient != null) webClient.close();
    }

    public abstract WebTarget getTarget();
    public abstract void setTarget(WebTarget target);

    public Invocation<Response, MediaType> getRequest(MediaType type){
        if (getTarget() != null){
            return new InvocationBuilder(type).addProperties(getProperties());
        }
        return null;
    }

    public Invocation<Response, MediaType> getJsonRequest(){
        return getRequest(MediaType.APPLICATION_JSON_TYPE);
    }

    public Invocation<Response, MediaType> getAuthorizedRequest(EntityInterface consume, MediaType type){
        if (getTarget() != null){
            return new InvocationBuilder(type, consume).addProperties(getProperties());
        }
        return null;
    }

    public Invocation<Response, MediaType> getAuthorizedJsonRequest(EntityInterface consume){
        return getAuthorizedRequest(consume, MediaType.APPLICATION_JSON_TYPE);
    }

    public  <T extends EntityInterface> T inflate(Response response, Class<T> type) throws IOException, HttpInvocationException {
        generateThrowable(response);
        String responseAsString = response.readEntity(String.class);
        List items = inflateJson(responseAsString, type);
        return (items != null && items.size() > 0) ? (T) items.get(0) : null;
    }

    public void generateThrowable(Response response) throws HttpInvocationException {
        if (response.getStatusInfo() == Response.Status.INTERNAL_SERVER_ERROR){
            throw new HttpInvocationException("Internal Server Error!");
        }
    }

}
