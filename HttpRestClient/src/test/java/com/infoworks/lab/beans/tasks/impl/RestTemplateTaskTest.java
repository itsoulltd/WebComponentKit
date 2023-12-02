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
import com.infoworks.lab.rest.template.Invocation;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
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
        stack.push(new GetRequest(template, new Person()));
        stack.push(new PostRequest(template, new Person(), "/api/save"));
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
    public void aggregatedRequestTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //
        HttpTemplate template = new PersonRestTemplate();

        TaskStack stack = TaskStack.createSync(true);
        stack.push(new AggregateRequest(template, Invocation.Method.GET, new Person()
                , new QueryParam("/api", null), new QueryParam("id", "121")));
        stack.push(new AggregateRequest(template, Invocation.Method.POST, new Person()
                , new QueryParam("/api", null), new QueryParam("save", null)));
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

    private static class PersonRestTemplate extends HttpRepositoryTemplate<Person, String> {

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
        public Class<Person> getEntityType() {
            return Person.class;
        }

        @Override
        protected List<Person> unmarshal(String json) throws IOException {
            return null;
        }

        private static Random random = new Random(1232);

        @Override
        public List<Person> fetch(Integer page, Integer limit) throws RuntimeException {
            List<Person> res = new ArrayList<>();
            res.add(new Person("MyName", random.nextInt()));
            return res;
        }
    }

    private static class Person extends Response {

        private String name;
        private Integer age;

        //For Jackson Serializer need empty constructor:
        public Person() {}

        public Person(String name, Integer age) {
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
