package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.beans.tasks.rest.client.spring.methods.*;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.sql.query.models.Row;
import org.junit.Test;

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
        RestTask task = new GetTask(
                "http://localhost:8080/user"
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