package com.infoworks.lab.beans.queue.fakejms;

public interface JMSBrokerListener {
    void startListener(String message);
    void abortListener(String message);
}
