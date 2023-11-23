package com.infoworks.lab.beans.queue.fakejms;

import com.infoworks.lab.beans.queue.AbstractTaskQueueManager;
import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JMSQueueManager extends AbstractTaskQueueManager implements JMSBrokerListener {

    private final ExecutorService exeQueue;
    private final ExecutorService abortQueue;

    public JMSQueueManager(QueuedTaskLifecycleListener listener, int numberOfThreads) {
        super(listener);
        numberOfThreads = numberOfThreads <= 0
                ? (Runtime.getRuntime().availableProcessors() / 2)
                : numberOfThreads;
        this.exeQueue = Executors.newFixedThreadPool(numberOfThreads);
        this.abortQueue = Executors.newFixedThreadPool(numberOfThreads);
    }

    @Override
    protected Task createTask(String text) throws ClassNotFoundException, IOException
            , IllegalAccessException, InstantiationException
            , NoSuchMethodException, InvocationTargetException {
        Task task = super.createTask(text);
        //Inject dependency into Task during MOM's task execution.
        return task;
    }

    @Override
    public void startListener(String msg) {
        exeQueue.submit(() -> handleTextOnStart(msg));
    }

    @Override
    public void abortListener(String msg) {
        abortQueue.submit(() -> handleTextOnStop(msg));
    }
}
