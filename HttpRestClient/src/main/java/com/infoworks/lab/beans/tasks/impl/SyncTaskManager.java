package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.TimeUnit;

public class SyncTaskManager implements TaskManager  {

    private TaskLifecycleListener listener;

    public SyncTaskManager() {}

    public SyncTaskManager(TaskLifecycleListener listener) {
        this.listener = listener;
    }

    public TaskLifecycleListener getListener() {
        return listener;
    }

    public void setListener(TaskLifecycleListener listener) {
        this.listener = listener;
    }

    @Override @SuppressWarnings("Duplicates")
    public void start(Task task, Message message) {
        //
        if (getListener() != null)
            getListener().before(task, TaskManager.State.Forward);
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
                if (getListener() != null) {
                    Message converted = task.convert(result);
                    getListener().finished(converted);//TERMINATION:
                }
            }else{
                Message converted = task.convert(result);
                start(task.next(), converted);//START-NEXT:
            }
        }
        //
    }

    @Override
    public void stop(Task task, Message reason) {
        //
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
        //
    }

    @Override
    public void terminateRunningTasks(long timeout, TimeUnit unit) {}

    @Override
    public void close() throws Exception {
        terminateRunningTasks(0l, TimeUnit.SECONDS);
    }

}
