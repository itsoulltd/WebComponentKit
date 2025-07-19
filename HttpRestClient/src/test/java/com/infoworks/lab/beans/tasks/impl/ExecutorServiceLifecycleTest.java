package com.infoworks.lab.beans.tasks.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceLifecycleTest {

    public static void main(String[] args) {
        int job_count = 5;
        ExecutorService service = Executors.newFixedThreadPool(2);
        List<Future<String>> handlers = new ArrayList<>();

        for (int i = 0; i < job_count; i++) {
            Future<String> handle = service.submit(new MyTask());
            handlers.add(handle);
        }

        //When call shutdown() on service:
        //1. returns immediately
        //2. cause subsequent submit or execute request to be rejected
        //3. pool moves to shutdown state
        //4. when (if) all the currently submitted task complete, pool moves to terminate state.
        service.shutdown();

        //Note: If we do not call service.shutdown(), it means the non-daemon threads in pool are alive,
        //So the main thread shall be alive, unless we force quite.
    }

}

/**
 * MyTask of Type Callable<String>
 */
class MyTask implements Callable<String> {

    private static int nextId = 0;
    private int jobId = nextId++;

    @Override
    public String call() throws Exception {
        System.out.println("Job: " + jobId + " starting.");
        //Long Running State:
        try {
            Thread.sleep((int) Math.random() * 2000 + 1000);
        } catch (InterruptedException e) {
            System.out.println("Job: " + jobId + " received shutdown request.");
            return "Job: " + jobId + " early shutdown result.";
        }
        //Arbitrary Exception (May happened) State:
        if (Math.random() > 0.7) {
            System.out.println("Job: " + jobId + " throwing exception.");
            throw new SQLException("Job: " + jobId + " Database un-reachable.");
        }
        //Final stage:
        System.out.println("Job: " + jobId + " complete normally.");
        return "Job: " + jobId + " normal result.";
    }
}
