package com.infoworks.lab.domain.beans.queue;

import com.infoworks.lab.rest.models.Message;

/**
 * Defined:JmsMessage Protocol
 */

public class JmsMessage extends Message {

    private String taskClassName;
    private String messageClassName;
    private String errorClassName;
    private String errorPayload;

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

    public String getErrorPayload() {
        return errorPayload;
    }

    public JmsMessage setErrorPayload(String errorPayload) {
        this.errorPayload = errorPayload;
        return this;
    }

    public String getErrorClassName() {
        return errorClassName;
    }

    public JmsMessage setErrorClassName(String errorClassName) {
        this.errorClassName = errorClassName;
        return this;
    }
}
