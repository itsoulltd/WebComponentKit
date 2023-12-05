package com.infoworks.lab.beans.tasks.rest.client.spring;

import com.infoworks.lab.beans.tasks.rest.client.base.BaseRequest;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class HttpRequest extends BaseRequest<Message, Response> {

    private RestTemplate template;
    private RequestEntity consume;
    private QueryParam[] params;
    private Class<? extends Response> responseType;

    public HttpRequest() {}

    public HttpRequest(RestTemplate template
            , RequestEntity consume
            , Class<? extends Response> responseType
            , QueryParam...params) {
        this.template = template;
        this.consume = consume;
        this.responseType = responseType;
        this.params = params;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        int responseCode = 500;
        try {
            if (params != null && params.length > 0) {
                String queryParam = urlencodedQueryParam(params);
                String domain = consume.getUrl().toString();
                if(domain.endsWith("/")) domain = domain.substring(0, domain.length() - 1);
                String rootUri = domain + queryParam;
                consume = new RequestEntity(consume.getBody()
                        , consume.getHeaders()
                        , consume.getMethod()
                        , new URI(rootUri));
            }
            ResponseEntity<String> rs = template.exchange(consume, String.class);
            responseCode = rs.getStatusCodeValue();
            String responseAsString = rs.getBody();
            List<Response> returnList = inflateJson(responseAsString, (Class<Response>) responseType);
            return returnList.get(0);
        } catch (Exception e) {
            return new Response().setStatus(responseCode).setError(e.getMessage());
        }
    }
}
