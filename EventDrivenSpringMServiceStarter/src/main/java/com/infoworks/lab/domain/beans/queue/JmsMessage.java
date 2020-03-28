package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.rest.models.Message;

/**
 * Defined:JmsMessage Protocol
 */

public class JmsMessage extends Message {

    private String taskClassName;
    private String messageClassName;

    public String getTaskClassName() {
        return taskClassName;
    }

    public JmsMessage setTaskClassName(String taskClassName) {
        this.taskClassName = taskClassName;
        return this;
    }

    public String getMessageClassName() {
        return messageClassName;
    }

    public JmsMessage setMessageClassName(String messageClassName) {
        this.messageClassName = messageClassName;
        return this;
    }
}
