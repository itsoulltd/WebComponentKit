package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.TransactionQueue;
import com.infoworks.lab.rest.models.Message;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public interface TaskQueue {

    static <STACK extends TaskQueue> TaskQueue create(String typeName){
        try {
            Class<STACK> type = (Class<STACK>) Class.forName(typeName, true, ClassLoader.getSystemClassLoader());
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    static <STACK extends TaskQueue> TaskQueue create(Class<STACK> type){
        return create(type.getName());
    }

    static TaskQueue create(){
        return create(TransactionQueue.class);
    }

    static TaskQueue createSync(boolean sync){
        return new TransactionQueue(sync);
    }

    static TaskQueue createSync(boolean sync, ExecutorService service){
        return new TransactionQueue(sync, service);
    }

    TaskQueue add(Task task);
    TaskQueue cancel(Task task);
    void onTaskComplete(BiConsumer<Message, TaskStack.State> onComplete);
    void onTaskComplete(TaskCompletionListener onComplete);
}
