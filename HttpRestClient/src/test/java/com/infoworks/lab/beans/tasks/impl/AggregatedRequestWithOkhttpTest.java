package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.rest.aggregate.AggregateRequest;
import com.infoworks.lab.beans.tasks.rest.aggregate.AggregatedResponse;
import com.infoworks.lab.client.okhttp.HttpTemplate;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.template.HttpInteractor;
import com.infoworks.lab.rest.template.Invocation;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AggregatedRequestWithOkhttpTest {

    @Test
    public void aggregatedOkHttpRequestTest() throws MalformedURLException {
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
