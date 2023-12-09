package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.TransactionStack;
import com.infoworks.lab.rest.models.Message;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public interface TaskStack {

    enum State{
        None,
        Running,
        Finished,
        Failed,
        Canceled
    }

    static <STACK extends TaskStack> TaskStack create(String typeName) {
        try {
            Class<STACK> type = (Class<STACK>) Class.forName(typeName
                    , true
                    , ClassLoader.getSystemClassLoader());
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static <STACK extends TaskStack> TaskStack create(Class<STACK> type) {
        return create(type.getName());
    }

    static TaskStack create(){
        return create(TransactionStack.class);
    }

    static TaskStack createSync(boolean sync){
        return new TransactionStack(sync);
    }

    static TaskStack createAsync(ExecutorService service){
        return new TransactionStack(false, service);
    }

    TaskStack push(Task task);
    void commit(boolean reverse, BiConsumer<Message, State> onComplete);
    void commit(boolean reverse, TaskCompletionListener onComplete);
    void cancel();
}
