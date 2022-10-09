package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

public interface QueuedTaskLifecycleListener extends TaskLifecycleListener {
    void abort(Task task, Message error);
}
