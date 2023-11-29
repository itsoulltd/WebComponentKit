package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.*;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.rest.template.Invocation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public abstract class HttpRepositoryTemplate<E extends Response, ID> extends HttpTemplate<E, Message> implements RestRepository<E, ID> {

    public HttpRepositoryTemplate(Object... config) {
        super(config);
    }

    protected abstract String schema();

    protected abstract String host();

    protected abstract Integer port();

    protected abstract String api();

    public abstract String getPrimaryKeyName();

    public abstract Class<E> getEntityType();

    public ItemCount rowCount() {
        try {
            javax.ws.rs.core.Response response = execute(null, Invocation.Method.GET, "rowCount");
            ItemCount iCount = inflate(response, ItemCount.class);
            return iCount;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ItemCount();
    }

    public List<E> fetch(Integer page, Integer limit) throws RuntimeException{
        try {
            Response items = get(null, new QueryParam("page", page.toString()), new QueryParam("limit", limit.toString()));
            if (items instanceof ResponseList){
                List<E> collection = ((ResponseList)items).getCollections();
                return collection;
            }
        } catch (HttpInvocationException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    public E insert(E ent) throws RuntimeException{
        try {
            E response = post(ent);
            return response;
        } catch (HttpInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    public E update(E ent, ID id) throws RuntimeException{
        try {
            E response = put(ent);
            return response;
        } catch (HttpInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(ID id) throws RuntimeException{
        return delete(id, new Message().setPayload(""));
    }

    public boolean delete(ID id, Message token) throws RuntimeException{
        try {
            boolean isDeleted = delete(token, new QueryParam(getPrimaryKeyName(), id.toString()));
            return isDeleted;
        } catch (HttpInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    public List<E> search(SearchQuery searchQuery) throws RuntimeException{
        List<E> ent;
        try {
            ent = secureSearch(searchQuery);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (HttpInvocationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ent;
    }

    protected List<E> secureSearch(SearchQuery query) throws IOException, HttpInvocationException {
        javax.ws.rs.core.Response response = execute(query
                , Invocation.Method.POST
                , "search");
        String responseStr = response.readEntity(String.class);
        List<E> ent = unmarshal(responseStr);
        return ent;
    }

    protected abstract List<E> unmarshal(String json) throws IOException;

}
