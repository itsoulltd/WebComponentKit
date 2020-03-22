package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.beans.tasks.impl.TransactionStack;
import com.infoworks.lab.rest.models.Message;

import java.util.function.BiConsumer;

public interface TaskStack {

    enum State{
        None,
        Running,
        Finished,
        Failed,
        Canceled
    }

    static <STACK extends TaskStack> TaskStack create(Class<STACK> type){
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static TaskStack create(){
        return create(TransactionStack.class);
    }

    TaskStack push(Task task);
    void commit(boolean reverse, BiConsumer<Message, State> onComplete);
    void commit(boolean reverse, TaskCompletionListener onComplete);
    void cancel();
}
