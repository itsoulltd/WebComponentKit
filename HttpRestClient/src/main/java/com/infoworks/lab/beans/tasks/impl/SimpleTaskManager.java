package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleTaskManager implements TaskManager {

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private TaskLifecycleListener listener;

    public SimpleTaskManager() {}

    public SimpleTaskManager(TaskLifecycleListener listener) {
        this.listener = listener;
    }

    public TaskLifecycleListener getListener() {
        return listener;
    }

    public void setListener(TaskLifecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(Task task, Message message) {
        service.submit(() -> {
            if (getListener() != null)
                getListener().beforeStart(task, State.Forward);
            //Call Execute:
            Message result = null;
            boolean mustAbort = false;
            try {
                result = task.execute(message);
            }catch (RuntimeException e) {
                mustAbort = true;
                result = new Message();
                result.setPayload(String.format("{\"error\":\"%s\", \"status\":500}", e.getMessage()));
            }
            //
            if (task.next() == null){
                if (getListener() != null)
                    getListener().finished(result);
            }else{
                if (mustAbort){
                    if (getListener() != null)
                        getListener().beforeEnd(task, State.Backward);
                    stop(task.next(), result);
                }else {
                    if (getListener() != null)
                        getListener().beforeEnd(task, State.Forward);
                    //
                    Message converted = task.converter() != null
                            ? task.converter().apply(result) : result;
                    start(task.next(), converted);
                }
            }
        });
    }

    @Override
    public void stop(Task task, Message reason) {
        service.submit(() -> {
            if (getListener() != null)
                getListener().beforeStart(task, State.Backward);
            //Call Abort:
            Message result = null;
            try {
                result = task.abort(reason);
            }catch (RuntimeException e) {}
            //
            if (task.next() == null){
                if (getListener() != null)
                    getListener().failed(result);
            }else{
                if (getListener() != null)
                    getListener().beforeEnd(task, State.Backward);
                stop(task.next(), result);
            }
        });
    }
}
