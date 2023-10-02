package com.infoworks.lab.beans.queue;

/**
 * Handle simulation for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka
 */
public interface EventQueueListener {
    void startListener(String msg);
    void abortListener(String msg);
}
