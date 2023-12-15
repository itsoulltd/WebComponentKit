package com.infoworks.lab.beans.tasks.rest.aggregate;

import com.infoworks.lab.beans.tasks.rest.client.base.BaseRequest;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import com.it.soul.lab.sql.entity.EntityInterface;

public class AggregateRequest<P extends Response, C extends EntityInterface> extends BaseRequest<Message, AggregatedResponse<P>> {

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
}
