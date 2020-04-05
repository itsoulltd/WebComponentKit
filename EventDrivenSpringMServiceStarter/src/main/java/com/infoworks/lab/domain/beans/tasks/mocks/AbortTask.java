package com.infoworks.lab.domain.beans.tasks.mocks;

import com.infoworks.lab.domain.beans.tasks.base.AbstractTask;
import com.infoworks.lab.rest.models.Message;

import java.util.Random;
import java.util.logging.Logger;

public class AbortTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger("AbortTask");

    @Override
    public Message execute(Message message) throws RuntimeException {
        //logger.info("RUNNING ON " + Thread.currentThread().getName());
        throw new RuntimeException("This is AbortTask!! (" + getMessage().getPayload() + ")");
    }

    @Override
    public Message abort(Message message) throws RuntimeException {
        //logger.info("RUNNING ON " + Thread.currentThread().getName());
        logger.info("DOING ABORT:" + ((getMessage() != null) ? getMessage().toString() : ""));
        Random RANDOM = new Random();
        int rand = RANDOM.nextInt(6) + 1;
        try {
            Thread.sleep(rand * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }
}
