package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;
import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskManager;
import com.infoworks.lab.rest.models.Message;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SynchQueueManager implements TaskManager {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private QueuedTaskLifecycleListener listener;

    public SynchQueueManager() {}

    public SynchQueueManager(QueuedTaskLifecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(Task task, Message message) {
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Forward);
            //Call Execute:
            boolean mustAbort = false;
            Message msg = null;
            try {
                msg = task.execute(message);
            } catch (RuntimeException e) {
                mustAbort = true;
                msg = new Message();
                msg.setPayload(String.format("{\"error\":\"%s\", \"status\":500}", e.getMessage()));
            }
            //End Execute:
            if (getListener() != null) {
                if (mustAbort) {
                    getListener().abort(task, msg);
                } else {
                    getListener().after(task, State.Forward);
                    getListener().finished(msg);
                }
            }
        }
    }

    @Override
    public void stop(Task task, Message reason) {
        if (task != null){
            if (getListener() != null)
                getListener().before(task, State.Backward);
            //Call Execute:
            Message msg = task.abort(reason);
            //End Execute:
            if (getListener() != null) {
                getListener().after(task, State.Backward);
                getListener().failed(msg);
            }
        }
    }

    @Override
    public void terminateRunningTasks(long timeout, TimeUnit unit) {
        //TODO:
        //send termination to jms-template for stopping current processing or abandon all active task from
        // exeQueue:
    }

    @Override
    public void close() throws Exception {
        //TODO:
        //Clean of any resource:
    }

    public QueuedTaskLifecycleListener getListener() {
        return listener;
    }

    public void setListener(QueuedTaskLifecycleListener listener) {
        this.listener = listener;
    }
}
