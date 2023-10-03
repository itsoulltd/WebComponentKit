package com.infoworks.lab.beans.queue.fakejms;

/**
 * Handle simulation for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka
 */
public interface JMSQueueListener {
    void startListener(String msg);
    void abortListener(String msg);
}
