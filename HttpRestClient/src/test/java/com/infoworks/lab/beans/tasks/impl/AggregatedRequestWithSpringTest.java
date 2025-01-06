package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.rest.aggregate.AggregateRequest;
import com.infoworks.lab.beans.tasks.rest.aggregate.AggregatedResponse;
import com.infoworks.lab.client.spring.HttpTemplate;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AggregatedRequestWithSpringTest {

    @Test
    public void aggregatedSpringRequestItemCountTest() throws MalformedURLException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        HttpInteractor templateA = new HttpTemplate(
                new URL("http://localhost:8080/passenger/rowCount"), ItemCount.class);

        HttpInteractor templateB = new HttpTemplate(
                new URL("http://localhost:8080/passenger"), ItemCount.class);

        TaskStack stack = TaskStack.createSync(true);
        stack.push(new AggregateRequest(templateA, Invocation.Method.GET
                , null));
        stack.push(new AggregateRequest(templateB, Invocation.Method.GET
                , null
                , new QueryParam("rowCount", null)));

        //
        stack.commit(true, (message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("State: " + status);
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

    @Test
    public void aggregatedSpringRequestTest() throws MalformedURLException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        HttpInteractor template = new HttpTemplate(
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

    @Test
    public void aggregatedSpringRequestWithSequentialCompletableFutureTest() throws MalformedURLException {
        ExecutorService queue = Executors.newSingleThreadExecutor();

        HttpInteractor template = new HttpTemplate(
                new URL("http://localhost:8080/passenger"), Passenger.class);

        HttpInteractor itemCountTemplate = new com.infoworks.lab.client.spring.HttpTemplate(
                new URL("http://localhost:8080/passenger/rowCount"), ItemCount.class);

        AggregatedResponse<Response> aggResponse = new AggregatedResponse<>();

        CompletableFuture.allOf(
                //Rest Call 01: Insert Passenger
                CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(template
                            , Invocation.Method.POST
                            , new Passenger("Towhid", 39));
                    return request.execute(aggResponse);
                }, queue).thenAccept(response -> System.out.println("Insert status: " + response.getStatus()))

                //Rest Call 02: After Insert
                , CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(itemCountTemplate, Invocation.Method.GET
                            , null);
                    return request.execute(aggResponse);
                }, queue).thenAccept(response -> System.out.println("ItemCount status: " + response.getStatus()))

                //Rest Call 03: Delete Passenger
                , CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(template, Invocation.Method.DELETE
                            , null
                            , new QueryParam("name", "Towhid"));
                    return request.execute(aggResponse);
                }, queue).thenAccept(response -> System.out.println("Delete status: " + response.getStatus()))

                //Rest Call 04: After Delete
                , CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(itemCountTemplate, Invocation.Method.GET
                            , null);
                    return request.execute(aggResponse);
                }, queue).thenAccept(response -> System.out.println("ItemCount status: " + response.getStatus()))
        ).join();

        //Print Aggregated response:
        aggResponse.forEach(response -> System.out.println(response.toString()));
    }

    @Test
    public void aggregatedSpringRequestWithParallelCompletableFutureTest() throws MalformedURLException {
        HttpInteractor template = new HttpTemplate(
                new URL("http://localhost:8080/passenger"), Passenger.class);

        AggregatedResponse<Response> aggResponse = new AggregatedResponse<>();

        CompletableFuture.allOf(
                //Rest Call 01:
                CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(template, Invocation.Method.GET
                            , null
                            , new QueryParam("page", "0"), new QueryParam("limit", "2"));
                    return request.execute(aggResponse);
                }).thenAccept(response -> System.out.println("Page_0, limit_2 status: " + response.getStatus()))
                //Rest Call 02:
                , CompletableFuture.supplyAsync(() -> {
                    AggregateRequest request = new AggregateRequest(template, Invocation.Method.GET
                            , null
                            , new QueryParam("page", "1"), new QueryParam("limit", "2"));
                    return request.execute(aggResponse);
                }).thenAccept(response -> System.out.println("Page_1, limit_2 status: " + response.getStatus()))
        ).join();

        //Print Aggregated response:
        aggResponse.forEach(response -> System.out.println(response.toString()));
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
}
