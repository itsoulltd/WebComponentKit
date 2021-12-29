package com.infoworks.lab.rest.repository;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.*;
import com.infoworks.lab.rest.template.Interactor;
import com.infoworks.lab.rest.template.Invocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestRepositoryTest {

    private PassengerRepository repository;

    public PassengerRepository getRepository() {
        if (repository == null){
            try {
                repository = Interactor.create(PassengerRepository.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return repository;
    }

    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    @Before
    public void before() {
        env.set("app.passenger.host", "localhost");
        env.set("app.passenger.port", "8080");
        env.set("app.passenger.api", "passenger");
    }

    @Test
    public void envTest(){
        Assert.assertTrue(System.getenv("app.passenger.host").equalsIgnoreCase("localhost"));
        Assert.assertTrue(System.getenv("app.passenger.port").equalsIgnoreCase("8080"));
        Assert.assertTrue(System.getenv("app.passenger.api").equalsIgnoreCase("passenger"));
    }

    @Test
    public void rowCount() {
        ItemCount count = getRepository().rowCount();
        System.out.println(count.getCount());
    }

    @Test
    public void fetch() {
        ItemCount count = getRepository().rowCount();
        int max = count.getCount().intValue();
        int offset = 0;
        int limit = 5;
        int numOfPage = (max / limit) + 1;
        while (offset < numOfPage){
            List<Passenger> riders = getRepository().fetch(offset, limit);
            riders.forEach(rider -> System.out.println(rider.getName()));
            offset++;
        }
    }

    @Test
    public void doa() throws HttpInvocationException {
        //Create & Insert:
        Passenger created = getRepository()
                .insert(new Passenger("Tictoc", Gender.NONE, 18));
        if(created != null) {
            System.out.println("Created: " + created.getName());
            //Update:
            created.setName("Tictoc-up");
            Passenger update = getRepository().update(created, created.getId());
            if (update != null){
                System.out.println("Updated: " + update.getName());
                //Delete:
                boolean isDeleted = getRepository().delete(update.getId());
                System.out.println("Is Deleted : " + isDeleted);
            }
        }
    }

    public static class PassengerRepository extends HttpTemplate<Response, Message> implements RestRepository<Passenger, Integer> {

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
}