package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.TransactionQueue;
import com.infoworks.lab.rest.models.Message;

import java.util.function.BiConsumer;

public interface TaskQueue {

    static <STACK extends TaskQueue> TaskQueue create(Class<STACK> type){
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static TaskQueue create(){
        return create(TransactionQueue.class);
    }

    static TaskQueue createSync(boolean sync){
        return new TransactionQueue(sync);
    }

    TaskQueue add(Task task);
    TaskQueue cancel(Task task);
    void onTaskComplete(BiConsumer<Message, TaskStack.State> onComplete);
    void onTaskComplete(TaskCompletionListener onComplete);
}
