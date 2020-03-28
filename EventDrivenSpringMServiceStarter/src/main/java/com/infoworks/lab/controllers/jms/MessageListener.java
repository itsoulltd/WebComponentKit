package com.infoworks.lab.controllers.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MessageListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private static int retryCount = 0;

    @JmsListener(destination = "testQueue")
    public void listener(Message message) throws JMSException {
        // retrieve the message content
        TextMessage textMessage = (TextMessage) message;
        String text = textMessage.getText();
        if (text.isEmpty() || text.startsWith("jms")) {
            if (retryCount == 3){ //on 3rd attempt
                retryCount = 0;
                logger.log(Level.INFO, "Now Handled DLQ {0} ", text);
                message.acknowledge();
            }else{
                retryCount++;
                //Following has no effect if there are redelivery policy define with connection:
                //message.setJMSRedelivered(true);
                //bBy this we can set the expiration timestamp in case of failure
                //message.setJMSExpiration(123334342);
                throw new JMSException("Did't executed");
            }
        }else{
            //
            logger.log(Level.INFO, "Message received {0} ", text);
            message.acknowledge();
        }
    }

}
