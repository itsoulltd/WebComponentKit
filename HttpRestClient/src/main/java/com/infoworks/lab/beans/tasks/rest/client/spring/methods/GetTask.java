package com.infoworks.lab.beans.tasks.rest.client.spring.methods;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Consumer;

public class GetTask extends RestTask<Message, Response> {

    public GetTask() {super();}

    public GetTask(String baseUri, String requestUri, Object...params) {
        super(baseUri, requestUri, params);
    }

    public GetTask(String baseUri, String requestUri, Consumer<String> response) {
        super(baseUri, requestUri, response);
    }

    private Map<String, Object> paramsKeyMaps = new HashMap<>();

    private void updateRequestUriWithQueryParams(String requestUri, QueryParam[] params) {
        //Update paths?<query-params>
        String queryParam = urlencodedQueryParam(params);
        requestUri = requestUri.trim();
        if (requestUri.contains("?")) {
            String paths = requestUri.substring(0, requestUri.indexOf("?"));
            setRequestUri(paths + queryParam);
        } else {
            setRequestUri(requestUri + queryParam);
        }
    }

    public GetTask(String baseUri, String requestUri, QueryParam...params) {
        super(baseUri, requestUri);
        updateQueryParams(params);
    }

    public void updateQueryParams(QueryParam...params) {
        //Filter-Out null and empty:
        Arrays.stream(params)
                .filter(param -> param.getValue() != null && !param.getValue().isEmpty())
                .forEach(param -> paramsKeyMaps.put(param.getKey(), param.getValue()));
        //
        List<QueryParam> paramList = new ArrayList<>();
        this.paramsKeyMaps.forEach((key, value) -> paramList.add(new QueryParam(key, value.toString())));
        updateRequestUriWithQueryParams(this.requestUri, paramList.toArray(new QueryParam[0]));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        RestTemplate template = getTemplate();
        try {
            ResponseEntity<String> response = (getParams().length > 0)
                    ? template.exchange(getUri(), HttpMethod.GET, getBody(), String.class, getParams())
                    : template.exchange(getUri(), HttpMethod.GET, getBody(), String.class);
            if (getResponseListener() != null)
                getResponseListener().accept(response.getBody());
            return (Response) new Response()
                    .setStatus(200)
                    .setMessage(getUri())
                    .setPayload(response.getBody());
        } catch (Exception e) {
            return new Response()
                    .setStatus(500)
                    .setMessage(getUri())
                    .setError(e.getMessage());
        }
    }
}
