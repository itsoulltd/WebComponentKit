package com.infoworks.lab.beans.tasks.rest.client.jersey;

import com.infoworks.lab.beans.tasks.rest.client.base.BaseRequest;
import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.IOException;
import java.net.MalformedURLException;

public class HttpRequest extends BaseRequest<Message, Response> {

    private HttpTemplate template;
    private EntityInterface consume;
    private Invocation.Method method;
    private QueryParam[] params;
    private Class<? extends Response> responseType;

    public HttpRequest() {}

    public HttpRequest(HttpTemplate template
            , EntityInterface consume
            , Class<? extends Response> responseType
            , Invocation.Method method
            , QueryParam...params) {
        this.template = template;
        this.consume = consume;
        this.method = method;
        this.params = params;
        this.responseType = responseType;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        int responseCode = 500;
        try {
            javax.ws.rs.core.Response res = template.execute(consume, method, params);
            responseCode = res.getStatus();
            String responseAsString = res.readEntity(String.class);
            Response response = Message.unmarshal(responseType, responseAsString);
            return response;
        } catch (MalformedURLException | HttpInvocationException e) {
            return new Response().setStatus(responseCode).setError(e.getMessage());
        } catch (IOException e) {
            return new Response().setStatus(responseCode).setError(e.getMessage());
        }
    }
}
