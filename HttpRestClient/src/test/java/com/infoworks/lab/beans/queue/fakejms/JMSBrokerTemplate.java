package com.infoworks.lab.beans.queue.fakejms;

import com.infoworks.lab.beans.tasks.definition.QueuedTaskLifecycleListener;

/**
 * Handle simulation for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka
 */
public class JMSBrokerTemplate {

    private final JMSBrokerListener listener;

    public JMSBrokerTemplate(QueuedTaskLifecycleListener listener, int numberOfThreads) {
        this.listener = new JMSQueueManager(listener, numberOfThreads);
    }

    void convertAndSend(String msg) {
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        listener.startListener(msg);
    }

    void send(String msg){
        //THIS IS FOR SIMULATION for MOM/AMQP/RabbitMQ/ActiveMQ/Redis/Kafka:
        listener.abortListener(msg);
    }
}
