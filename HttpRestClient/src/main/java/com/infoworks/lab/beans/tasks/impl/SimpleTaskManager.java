package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleTaskManager implements TaskManager {

    private ExecutorService service;
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

    public ExecutorService getService() {
        if (service == null){
             synchronized (this){
                 service = Executors.newSingleThreadExecutor();
             }
        }
        return service;
    }

    @Override
    public void start(Task task, Message message) {
        //
        getService().submit(() -> {
            if (getListener() != null)
                getListener().before(task, State.Forward);
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
            //End Execute:
            if (getListener() != null)
                getListener().after(task, State.Forward);
            //
            if (mustAbort){
                stop(task, result);//ABORT-SEQUENCE:
            }else {
                if (task.next() == null){
                    if (getListener() != null)
                        getListener().finished(result);//TERMINATION:
                }else{
                    Message converted = task.convert(result);
                    start(task.next(), converted);//START-NEXT:
                }
            }
        });
        //
    }

    @Override
    public void stop(Task task, Message reason) {
        //
        getService().submit(() -> {
            if (getListener() != null)
                getListener().before(task, State.Backward);
            //Call Abort:
            Message result = null;
            try {
                result = task.abort(reason);
            }catch (RuntimeException e) {}
            //End Abort:
            if (getListener() != null)
                getListener().after(task, State.Backward);
            //
            if (task.next() == null){
                if (getListener() != null)
                    getListener().failed(result);
            }else{
                stop(task.next(), result);
            }
        });
        //
    }
}
