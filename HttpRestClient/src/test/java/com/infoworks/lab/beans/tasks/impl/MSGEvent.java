package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.rest.models.events.Event;

public class MSGEvent  extends Event {
    private String message;
    private int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
