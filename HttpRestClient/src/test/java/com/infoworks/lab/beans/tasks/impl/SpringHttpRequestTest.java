package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.rest.client.spring.HttpRequest;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class SpringHttpRequestTest {

    @Test
    public void requestSpringFlowTest() throws URISyntaxException {
        CountDownLatch latch = new CountDownLatch(3);
        //
        RestTemplate template = new RestTemplate();
        String rootUri = "http://localhost:8080/passenger";

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

        RequestEntity consume = new RequestEntity(HttpMethod.GET, new URI(rootUri));
        queue.add(new HttpRequest(template
                    , consume
                    , Passenger.class
                    , new QueryParam("page", "0"), new QueryParam("limit", "10")));

        consume = new RequestEntity(new Passenger("Sohana", 29), HttpMethod.POST, new URI(rootUri));
        queue.add(new HttpRequest(template
                    , consume
                    , Passenger.class));

        consume = new RequestEntity(HttpMethod.DELETE, new URI(rootUri));
        queue.add(new HttpRequest(template
                    , consume
                    , Passenger.class
                    , new QueryParam("name", "Sohana")));
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    ////////////////////////////////////////////////////////////////////

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
