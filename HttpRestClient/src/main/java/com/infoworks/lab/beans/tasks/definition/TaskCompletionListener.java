package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

public interface TaskCompletionListener {
    void failed(Message reason);
    void finished(Message result);
}
