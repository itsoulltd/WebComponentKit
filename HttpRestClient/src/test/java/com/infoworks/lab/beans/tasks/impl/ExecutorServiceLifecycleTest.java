package com.infoworks.lab.beans.tasks.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceLifecycleTest {

    public static void main(String[] args) {
        int jobCount = 5;
        ExecutorService service = Executors.newFixedThreadPool(2);
        List<Future<String>> futureMessages = new ArrayList<>();

        //1. Submit all tasks:
        for (int i = 0; i < jobCount; i++) {
            Future<String> future = service.submit(new MyTask());
            futureMessages.add(future);
        }

        //6. Cancel a job, that's in mid-run:
        /*try {
            Thread.sleep(800);
            Future<String> secondFuture = futureMessages.get(1);
            secondFuture.cancel(true);
        } catch (InterruptedException e) {
            System.out.println("main thread interrupted?");
        }*/

        //2. When call shutdown() on service:
        //a. returns immediately
        //b. cause subsequent submit or execute request to be rejected
        //c. pool moves to shutdown state
        //d. when (if) all the currently submitted task complete, pool moves to terminate state.
        service.shutdown();

        //3. What if we call shutdownNow()
        //service.shutdownNow();

        //Note: If we do not call service.shutdown(), it means the non-daemon threads in pool are alive,
        //So the main thread shall be alive, unless we force quite.

        //5. Receives the results:
        System.out.println("All jobs submitted to the pool:");
        while (futureMessages.size() > 0) {
            Iterator<Future<String>> iterator = futureMessages.iterator();
            while (iterator.hasNext()) {
                Future<String> now = iterator.next();
                if (now.isDone()) {
                    iterator.remove();
                    try {
                        String result = now.get();
                        System.out.println("Got a job result: " + result);
                    } catch (InterruptedException e) {
                        //should never happened:
                        System.out.println("main thread interrupted??");
                    } catch (ExecutionException e) {
                        System.out.println("Job throw an exception: " + e.getCause());
                    } catch (CancellationException e) {
                        System.out.println("Job was canceled!");
                    }
                }
            }//
        }
        //End of While-01

        //4. Await until termination:
        try {
            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.out.println("main thread interrupted???");
        }
        System.out.println("main exiting!");
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
