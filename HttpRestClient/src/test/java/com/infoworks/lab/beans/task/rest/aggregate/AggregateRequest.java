package com.infoworks.lab.beans.task.rest.aggregate;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggregateRequest<C extends EntityInterface, P extends Response> extends ExecutableTask<Message, AggregatedResponse<P>> {

    private HttpInteractor<P, C> template;
    private C consume;
    private QueryParam[] params;
    private Invocation.Method method;

    public AggregateRequest() {}

    public AggregateRequest(HttpInteractor<P, C> template, Invocation.Method method, C consume, QueryParam... params) {
        this.template = template;
        this.consume = consume;
        this.params = params;
        this.method = method;
    }

    public void setTemplate(HttpInteractor<P, C> template) {
        this.template = template;
    }

    @Override
    public AggregatedResponse<P> execute(Message message) throws RuntimeException {
        if (template == null) throw new RuntimeException(AggregateRequest.class.getName() + " template is null!");
        if (message == null) {
            message = new AggregatedResponse();
        }
        P response = null;
        try {
            switch (method) {
                case POST:
                    response = template.post(consume, urlencodedQueryParam(params));
                    break;
                case PUT:
                    response = template.put(consume, urlencodedQueryParam(params));
                    break;
                case DELETE:
                    boolean isDeleted = template.delete(consume, params);
                    response = (P) new Response().setStatus(isDeleted ? 200 : 400)
                            .setMessage(isDeleted ? "Deletion Successful" : "Deletion Failed");
                    break;
                default:
                    response = template.get(consume, params);
            }
        } catch (Exception e) {
            response = (P) new Response().setStatus(500).setMessage(e.getMessage());
        }
        if (message instanceof AggregatedResponse) {
            //Add response to bag:
            ((AggregatedResponse) message).add(response);
        }
        return (AggregatedResponse<P>) message;
    }

    private String urlencodedQueryParam(QueryParam...params){
        if (params == null) return "";
        StringBuilder buffer = new StringBuilder();
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

    private StringBuilder validatePaths(String... params) {
        StringBuilder buffer = new StringBuilder();
        Arrays.stream(params).forEach(str -> {
            String trimmed = str.trim();
            if (trimmed.length() > 2 && trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
            if(trimmed.startsWith("/"))
                buffer.append(trimmed);
            else
                buffer.append("/" + trimmed);
        });
        return buffer;
    }
}
