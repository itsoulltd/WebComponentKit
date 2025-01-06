package com.infoworks.lab.beans.tasks.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.rest.repository.FetchRequest;
import com.infoworks.lab.beans.tasks.rest.repository.ItemCountRequest;
import com.infoworks.lab.beans.tasks.rest.repository.SearchRequest;
import com.infoworks.lab.beans.tasks.rest.request.DeleteRequest;
import com.infoworks.lab.beans.tasks.rest.request.GetRequest;
import com.infoworks.lab.beans.tasks.rest.request.PostRequest;
import com.infoworks.lab.client.jersey.HttpRepositoryTemplate;
import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.rest.template.HttpInteractor;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RestTemplateTaskTest {

    @Test
    public void requestJerseyFlowTest() {
        CountDownLatch latch = new CountDownLatch(6);
        //
        HttpTemplate template = new PersonRestTemplate(Passenger.class);

        TaskQueue queue = TaskQueue.createSync(true);
        queue.onTaskComplete((message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }
            latch.countDown();
        });

        //queue.add(new GetRequest(template, null, new QueryParam("page", "0"), new QueryParam("limit", "10")));
        //OR
        queue.add(new FetchRequest((RestRepository) template, 0, 10));

        queue.add(new PostRequest(template, new Passenger("Sohana", 29)));
        queue.add(new ItemCountRequest((RestRepository) template));

        SearchQuery query = Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC);
        query.add("name").isEqualTo("Sohana");
        queue.add(new SearchRequest((RestRepository) template, query));

        queue.add(new DeleteRequest(template, null, new QueryParam("name", "Sohana")));
        queue.add(new ItemCountRequest((RestRepository) template));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void requestOkHttpFlowTest() throws MalformedURLException {
        CountDownLatch latch = new CountDownLatch(3);
        //
        HttpInteractor template = new com.infoworks.lab.client.okhttp.HttpTemplate(
                new URL("http://localhost:8080/passenger"), Passenger.class);

        TaskQueue queue = TaskQueue.createSync(true);
        queue.onTaskComplete((message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }
            latch.countDown();
        });

        queue.add(new GetRequest(template, null, new QueryParam("page", "0"), new QueryParam("limit", "10")));
        queue.add(new PostRequest(template, new Passenger("Sohana", 29)));
        queue.add(new DeleteRequest(template, null, new QueryParam("name", "Sohana")));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void requestSpringFlowTest() throws MalformedURLException {
        CountDownLatch latch = new CountDownLatch(3);
        //
        HttpInteractor template = new com.infoworks.lab.client.spring.HttpTemplate(
                new URL("http://localhost:8080/passenger"), Passenger.class);

        TaskQueue queue = TaskQueue.createSync(true);
        queue.onTaskComplete((message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }
            latch.countDown();
        });

        queue.add(new GetRequest(template, null, new QueryParam("page", "0"), new QueryParam("limit", "10")));
        queue.add(new PostRequest(template, new Passenger("Sohana", 29)));
        queue.add(new DeleteRequest(template, null, new QueryParam("name", "Sohana")));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    ////////////////////////////////////////////////////////////////////

    private static class PersonRestTemplate extends HttpRepositoryTemplate<Passenger, String> {

        public PersonRestTemplate(Class<Passenger> aClass) {
            super(aClass);
        }

        @Override
        protected String schema() {
            return "http://";
        }

        @Override
        protected String host() {
            return "localhost";
        }

        @Override
        protected Integer port() {
            return 8080;
        }

        @Override
        protected String api() {
            return "/passenger";
        }

        @Override
        public String getPrimaryKeyName() {
            return "name";
        }

        @Override
        public Class<Passenger> getEntityType() {
            return Passenger.class;
        }

        @Override
        protected List<Passenger> unmarshal(String json) throws IOException {
            List<Passenger> list = Message.unmarshal(new TypeReference<List<Passenger>>() {}, json);
            return list;
        }
    }

    private static class Passenger extends Response {

        private Integer id = 0;
        private String name;
        private Integer age = 18;
        private String sex = "NONE";
        private Date dob = new java.sql.Date(new Date().getTime());
        private boolean active;

        //For Jackson Serializer need empty constructor:
        public Passenger() {}

        public Passenger(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        //For Jackson Serializer need all getter-setter:
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    ////////////////////////////////////////////////////////////////////

}
