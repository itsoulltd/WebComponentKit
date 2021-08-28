package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

public class TransactionQueue implements TaskQueue, QueuedTaskLifecycleListener {

    private final TaskManager manager;
    private TaskStack.State state = TaskStack.State.None;
    private BiConsumer<Message, TaskStack.State> callback;
    private TaskCompletionListener listener;
    private final Queue<Task> beanQueue;
    private final Queue<Task> abortQueue;

    public TransactionQueue() {
        this(false);
    }

    public TransactionQueue(boolean synch) {
        this.manager = (synch)
                ? TaskManager.createSyncQ(this)
                : TaskManager.createAsyncQ(this);
        beanQueue = new ConcurrentLinkedDeque<>();
        abortQueue = new ConcurrentLinkedDeque<>();
    }

    public TaskStack.State getState() {
        return state;
    }

    @Override
    public TaskQueue add(Task task) {
        beanQueue.add(task);
        if (getState() != TaskStack.State.Running){
            synchronized (this){
                state = TaskStack.State.Running;
            }
            manager.start(task, null);
        }
        return this;
    }

    @Override
    public void before(Task task, TaskManager.State state) {
        if (state == TaskManager.State.Forward){
            beanQueue.poll();
        }else if (state == TaskManager.State.Backward){
            abortQueue.poll();
        }
    }

    @Override
    public void after(Task task, TaskManager.State state) {
        if (!beanQueue.isEmpty()){
            Task cTask = beanQueue.peek();
            manager.start(cTask, null);
        }
        if (!abortQueue.isEmpty()){
            Task cTask = abortQueue.peek();
            manager.stop(cTask, null);
        }
        //
        if (beanQueue.isEmpty() && abortQueue.isEmpty()){
            synchronized (this){
                this.state = TaskStack.State.None;
            }
        }
    }

    @Override
    public void abort(Task task, Message error) {
        boolean isFirstTask = abortQueue.isEmpty();
        abortQueue.add(task);
        if (isFirstTask){
            manager.stop(task, error);
        }
    }

    @Override
    public TaskQueue cancel(Task task) {
        if (beanQueue.isEmpty())
            return this;
        if (beanQueue.contains(task)){
            beanQueue.remove(task);
        }
        return this;
    }

    @Override
    public void failed(Message reason) {
        try {
            if (callback != null){
                callback.accept(reason, TaskStack.State.Failed);
            }else if (listener != null){
                listener.failed(reason);
            }
        } catch (Exception e) {}
    }

    @Override
    public void finished(Message result) {
        try {
            if (callback != null){
                callback.accept(result, TaskStack.State.Finished);
            }else if (listener != null){
                listener.finished(result);
            }
        } catch (Exception e) {}
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> onComplete) {
        this.callback = onComplete;
    }

    @Override
    public void onTaskComplete(TaskCompletionListener onComplete) {
        this.listener = onComplete;
    }
}
