package com.infoworks.lab.rest.repository;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.*;
import com.infoworks.lab.rest.template.Invocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassengerRepository extends HttpTemplate<Response, Message> implements RestRepository<Passenger, Integer> {

    public PassengerRepository() {
        super(Passenger.class, Message.class);
    }

    @Override
    protected String schema() {
        return "http://";
    }

    @Override
    protected String host() {
        return System.getenv("app.passenger.host");
    }

    @Override
    protected Integer port() {
        return Integer.valueOf(System.getenv("app.passenger.port"));
    }

    @Override
    protected String api() {
        return System.getenv("app.passenger.api");
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    @Override
    public Class<Passenger> getEntityType() {
        return Passenger.class;
    }

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

    public List<Passenger> fetch(Integer offset, Integer limit){
        try {
            Response items = get(null, new QueryParam("offset", offset.toString()), new QueryParam("limit", limit.toString()));
            if (items instanceof ResponseList){
                List<Passenger> collection = ((ResponseList)items).getCollections();
                return collection;
            }
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Passenger insert(Passenger passenger){
        try {
            Passenger response = (Passenger) post(passenger);
            return response;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Passenger update(Passenger passenger, Integer userid){
        try {
            passenger.setId(userid);
            Passenger response = (Passenger) put(passenger);
            return response;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer userId){
        try {
            boolean isDeleted = delete(null, new QueryParam("userid", userId.toString()));
            return isDeleted;
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
