package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class SearchQueryTest {
    Message<SearchQuery> consume;
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = Message.getJsonSerializer();
        consume = new Message<>(SearchQuery.class);
    }

    @After
    public void tearDown() throws Exception {
        consume = null;
    }

    @Test
    public void parseTest(){
        consume.setEvent(Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary"));
        SearchQuery query = consume.getEvent();
        query.add("center")
                .isEqualTo("dasdad")
                .and("radius")
                .isEqualTo(500.0);

        String json = query.toString();
        System.out.println("marshal Result 1:"+ json);
        Assert.assertTrue("Working", json.isEmpty() == false);
    }

    @Test
    public void parseTest_2(){

        consume.setEvent(Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary"));
        SearchQuery query = consume.getEvent();
        query.setUuid(UUID.randomUUID().toString());
        query.add("emission-interval").isEqualTo(3000);
        query.add("center").isEqualTo("dasdad");
        query.add("radius").isEqualTo(500.0);

        String json = query.toString();
        System.out.println("marshal Result 1:"+ json);

        //Recreate Query from string
        Message<SearchQuery> nConsume = new Message(SearchQuery.class);
        nConsume.setPayload(json);

        SearchQuery rQuery = nConsume.getEvent();
        Assert.assertTrue(Objects.equals(query, rQuery));

        System.out.println("marshal Result after recreate:"+ rQuery.toString());

    }

    @Test
    public void createTest() throws IOException {

        consume.setEvent(Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary"));
        SearchQuery queryX = consume.getEvent();

        queryX.add("emission-interval").isEqualTo(3000);
        queryX.add("center").isEqualTo("dasdad");
        queryX.add("radius").isEqualTo(500.0);

        String json = queryX.toString();

        SearchQuery query = mapper.readValue(json, SearchQuery.class);

        Optional emission = query.get("emission-interval");
        Assert.assertTrue(emission.isPresent());

        Optional center = query.get("center");
        Assert.assertTrue(center.isPresent());

        Optional<Double> radius = query.get("radius", null);
        Assert.assertTrue(radius.isPresent());
        Assert.assertTrue(radius.get().equals(500.0));

        Optional nullVal = query.get("rdous");
        Assert.assertTrue(nullVal.isPresent() == false);

        //Add another one after search:
        queryX.add("adous").isEqualTo(500.0);
        query = mapper.readValue(queryX.toString(), SearchQuery.class);

        Optional wdousVal = query.get("adous");
        Assert.assertTrue(wdousVal.isPresent());

    }

    @Test
    public void booleanTest() throws IOException{
        //Data Simulation from Client:
        consume.setEvent(Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary"));
        SearchQuery queryX = consume.getEvent();
        queryX.add("TimeTicking").isEqualTo(false);
        String json = queryX.toString();

        //Now Parsing On ServerSide:
        SearchQuery reverseQ = mapper.readValue(json, SearchQuery.class);
        Optional<Object> timeTicking = reverseQ.get("TimeTicking");
        Assert.assertTrue(timeTicking.isPresent() == true);
        System.out.println("TimeTicking Value is '" + ((Boolean)timeTicking.get()) + "'");
    }

    @Test
    public void ConsumeTypeTest() throws IOException {
        consume.setEvent(Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC, "name","age","salary"));
        SearchQuery queryX = consume.getEvent();

        queryX.add("emission-interval").isEqualTo(3000);
        queryX.add("center").isEqualTo("dasdad");
        queryX.add("radius").isEqualTo(500.0);

        Person p = new Person();
        p.setActive(true);
        p.setName("James");
        p.setAge(18);
        queryX.add("person").isEqualTo(p);

        String json = queryX.toString();

        SearchQuery query = mapper.readValue(json, SearchQuery.class);
        Optional<Person> person = query.get("person", Person.class);
        Assert.assertTrue(person.isPresent());
        Assert.assertEquals(person.get().getName(), p.getName());
    }

    private static class Person extends Message{

        private String name;
        private int age;
        private boolean isActive;
        private String gender;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }
}