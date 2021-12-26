package com.infoworks.lab.rest.repository;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Gender;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.Passenger;
import com.infoworks.lab.rest.template.Interactor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

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
}