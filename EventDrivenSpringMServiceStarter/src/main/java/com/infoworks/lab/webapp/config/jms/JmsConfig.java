package com.infoworks.lab.webapp.config.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean @Autowired
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory){
        //Setting the DLQ redelivery policy:
        if (connectionFactory instanceof ActiveMQConnectionFactory) {
            RedeliveryPolicy redeliveryPolicy = ((ActiveMQConnectionFactory)connectionFactory).getRedeliveryPolicy();
            redeliveryPolicy.setInitialRedeliveryDelay(5000); //5 Seconds
            redeliveryPolicy.setBackOffMultiplier(2);
            redeliveryPolicy.setUseExponentialBackOff(true);
            redeliveryPolicy.setMaximumRedeliveries(3); //3 times redelivery attempt
        }
        //
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public Queue queue(){
        return new ActiveMQQueue("test-queue");
    }

    @Bean @Autowired
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            @Qualifier("jmsErrorHandler") ErrorHandler errorHandler) {
        //
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("2-3");
        factory.setErrorHandler(errorHandler);
        /**
         * The DMLC in spring has transactions enabled in your config, while in your Java example,
         * you have transactions disabled. Transactions overrides any acknowledge modes.
         * So, choose if you should go with transactions or client ack since you cannot pick both.
         */
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        //
        //This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        //
        return factory;
    }

}
