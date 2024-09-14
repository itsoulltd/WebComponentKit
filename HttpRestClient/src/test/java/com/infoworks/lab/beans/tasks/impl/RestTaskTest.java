package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.rest.client.spring.methods.*;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.sql.query.models.Row;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RestTaskTest {

    @Test
    public void getTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask task = new GetTask("http://localhost:8080/user"
                , "/rowCount", (res) -> {
            System.out.println(res);
        });
        queue.add(task);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void getFetchTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask task = new GetTask("http://localhost:8080/user"
                , "?limit={limit}&page={page}"
                , 10, 0);
        task.addResponseListener((res) -> System.out.println(res));
        queue.add(task);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void getFetchTaskWithQueryParamTest() {
        CountDownLatch latch = new CountDownLatch(1);
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask task = new GetTask("http://localhost:8080/user", ""
                , new QueryParam("limit", "10"), new QueryParam("page", "0"));
        task.addResponseListener((res) -> System.out.println(res));
        queue.add(task);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void login() {
        Row row = new Row()
                .add("username", "domain@app.com")
                .add("password", "******");
        //Type of tokens:
        List<String> types = Arrays.asList(
                //Will pass the test:
                null, "", "   ", "bearer ", "Bearer "
                //Shall not pass the test:
                , " Bearer ", "sdasdasffsdfsdffsf", " Bearer sdasdasffsdfsdffsf", " sdasdasffsdfsdffsf ");
        //Create a request:
        types.forEach(token -> {
            PostTask task = new PostTask("http://localhost:80/api/auth/auth/v1", "login");
            task.setBody(row, token);
            Response response = task.execute(null);
            System.out.println(response.getPayload());
        });
    }

    @Test
    public void postTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //TaskFlow:
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask postTask = new PostTask("http://localhost:8080/user"
                , "");
        postTask.setBody(new Row()
                        .add("accountName","towhid")
                        .add("email","m.towhid@gmail.com")
                        .add("secret","asrer545sdasda")
                        .add("tenantCharge","10.00")
                        .add("userCharge","0.00")
                , "my-token");
        postTask.addResponseListener((response) -> System.out.println(response));
        queue.add(postTask);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void putTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //TaskFlow:
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask putTask = new PutTask("http://localhost:8080/user"
                , "");
        putTask.setBody(new Row()
                        .add("id", "8")
                        .add("accountName","towhid")
                        .add("email","m.towhid@gmail.com")
                        .add("secret","asrer545sdasda")
                        .add("tenantCharge","10.00")
                        .add("userCharge","2.00")
                , "my-token");
        putTask.addResponseListener((response) -> System.out.println(response));
        queue.add(putTask);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void deleteTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //TaskFlow:
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask deleteTask = new DeleteTask(
                "http://localhost:8080/user"
                , "?id={id}"
                , 0);
        deleteTask.addResponseListener((response) -> System.out.println(response));
        queue.add(deleteTask);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    @Test
    public void searchTaskTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //TaskFlow:
        TaskQueue queue = TaskQueue.createSync(false);
        queue.onTaskComplete((message, state) -> {
            System.out.println("State: " + state);
            latch.countDown();
        });
        //
        RestTask task = new PostTask(
                "http://localhost:8080/user"
                , "/search");
        //Make query:
        SearchQuery query = Pagination.createQuery(SearchQuery.class, 10, SortOrder.DESC);
        query.add("name").isLike("%hana%");
        //
        task.setBody(query, "my-token");
        task.addResponseListener((response) -> System.out.println(response));
        queue.add(task);
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}