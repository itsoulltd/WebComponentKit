package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.task.rest.aggregate.AggregateRequest;
import com.infoworks.lab.beans.task.rest.aggregate.AggregatedResponse;
import com.infoworks.lab.beans.task.rest.repository.FetchRequest;
import com.infoworks.lab.beans.task.rest.request.GetRequest;
import com.infoworks.lab.beans.task.rest.request.PostRequest;
import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.client.jersey.HttpRepositoryTemplate;
import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class RestTemplateTaskTest {

    @Test
    public void requestFlowTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //
        HttpTemplate template = new PersonRestTemplate();

        TaskStack stack = TaskStack.createSync(true);
        stack.push(new FetchRequest((RestRepository) template, 1, 10));
        stack.push(new GetRequest(template, new Passenger()));
        stack.push(new PostRequest(template, new Passenger(), "/api/save"));
        //
        stack.commit(true, (message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                System.out.println(message.toString());
            }
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void aggregatedSpringRequestTest() throws MalformedURLException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        HttpInteractor template = new com.infoworks.lab.client.spring.HttpTemplate(
                new URL("http://localhost:8080/passenger"), Passenger.class);

        TaskStack stack = TaskStack.createSync(true);
        stack.push(new AggregateRequest(template, Invocation.Method.GET
                , null
                , new QueryParam("page", "0"), new QueryParam("limit", "10")));

        stack.push(new AggregateRequest(template, Invocation.Method.POST
                , new Passenger("Towhid", 19)));

        stack.push(new AggregateRequest(template, Invocation.Method.GET
                , null
                , new QueryParam("page", "0"), new QueryParam("limit", "10")));

        stack.push(new AggregateRequest(template, Invocation.Method.DELETE
                , null
                , new QueryParam("name", "Towhid")));
        //
        stack.commit(true, (message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                //System.out.println(message.toString());
                if (message instanceof AggregatedResponse) {
                    ((AggregatedResponse<Response>) message)
                            .forEach(val -> System.out.println(val.toString()));
                }
            }
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }


    ////////////////////////////////////////////////////////////////////

    private static class PersonRestTemplate extends HttpRepositoryTemplate<Passenger, String> {

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
            return "/api/person";
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
            return null;
        }

        private static Random random = new Random(1232);

        @Override
        public List<Passenger> fetch(Integer page, Integer limit) throws RuntimeException {
            List<Passenger> res = new ArrayList<>();
            res.add(new Passenger("MyName", random.nextInt()));
            return res;
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
