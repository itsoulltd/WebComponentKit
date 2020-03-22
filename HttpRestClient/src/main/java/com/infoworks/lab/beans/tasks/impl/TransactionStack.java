package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.*;
import com.infoworks.lab.rest.models.Message;

import java.util.Stack;
import java.util.function.BiConsumer;

public class TransactionStack implements TaskLifecycleListener, TaskStack {

    private final TaskManager manager;
    private final Stack<Task> beanStack;
    private final Stack<Task> passedStack;
    private State state = State.None;
    private BiConsumer<Message, State> callback;
    private TaskCompletionListener listener;

    public TransactionStack() {
        manager = TaskManager.create(this);
        beanStack = new Stack<>();
        passedStack = new Stack<>();
    }

    public State getState() {
        return state;
    }

    public synchronized void cancel(){
        if (state == State.Running){
            state = State.Canceled;
            if (passedStack.isEmpty()) return;
            manager.stop(passedStack.peek(), null);
        }
    }

    public synchronized TaskStack push(Task task){
        if (state == State.Running) return this;
        if (!beanStack.empty()){
            Task top = beanStack.peek();
            task.linkedTo(top);
        }
        beanStack.push(task);
        return this;
    }

    public synchronized void commit(boolean reverse, BiConsumer<Message, State> onComplete){
        this.callback = onComplete;
        commit(reverse);
    }

    public synchronized void commit(boolean reverse, TaskCompletionListener onComplete) {
        this.listener = onComplete;
        commit(reverse);
    }

    protected void commit(boolean reverse) {
        if (state == State.Running) return;
        if (beanStack.isEmpty()) return;
        if (reverse) {
            //TODO: reverse the beanStack order:
        }
        state = State.Running;
        manager.start(beanStack.peek(), null);
    }

    @Override
    public void before(Task task, TaskManager.State state) {
        synchronized (this){
            if (state == TaskManager.State.Forward){
                Task popped = beanStack.pop();
                passedStack.push(popped);
            }else if (state == TaskManager.State.Backward){
                Task popped = passedStack.pop();
                beanStack.push(popped);
            }
        }
    }

    @Override
    public void after(Task task, TaskManager.State state) {
        synchronized (this){
            if (state == TaskManager.State.Forward){
                //TODO:
            }else if (state == TaskManager.State.Backward){
                //TODO:
            }
        }
    }

    @Override
    public void failed(Message reason) {
        synchronized (this){
            state = State.Failed;
        }
        if (callback != null){
            callback.accept(reason, state);
        }else if (listener != null){
            listener.failed(reason);
        }
    }

    @Override
    public void finished(Message results) {
        synchronized (this){
            state = State.Finished;
            beanStack.clear();
            passedStack.clear();
        }
        if (callback != null){
            callback.accept(results, state);
        }else if (listener != null){
            listener.finished(results);
        }
    }

}