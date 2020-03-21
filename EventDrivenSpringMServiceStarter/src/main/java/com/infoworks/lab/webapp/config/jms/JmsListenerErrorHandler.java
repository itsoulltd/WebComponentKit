package com.infoworks.lab.webapp.config.jms;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component("jmsErrorHandler")
public class JmsListenerErrorHandler implements ErrorHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void handleError(Throwable throwable) {
        logger.log(Level.INFO, "JMS Message Failed!");
        logger.log(Level.WARNING, throwable.getMessage());
    }

}
