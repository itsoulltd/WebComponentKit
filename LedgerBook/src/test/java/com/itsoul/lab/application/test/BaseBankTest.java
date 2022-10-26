package com.itsoul.lab.application.test;

import com.itsoul.lab.application.bank.TheBank;
import com.itsoul.lab.application.bank.TheFixBank;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseBankTest {

    protected static Logger LOGGER = Logger.getLogger(TheBankTest.class.getSimpleName());

    protected void singleThreadTest(TheBank aBank) {
        aBank.newAccount("Alice-123", 10L);
        aBank.newAccount("Bob-456", 0L);

        Assert.assertEquals(10L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(0L, aBank.getBalance("Bob-456"));

        aBank.transfer("Alice-123", "Bob-456", 5L);

        Assert.assertEquals(5L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(5L, aBank.getBalance("Bob-456"));

        aBank.transfer("Alice-123", "Bob-456", 5L);

        Assert.assertEquals(0L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(10L, aBank.getBalance("Bob-456"));

        aBank.transfer("Alice-123", "Bob-456", 5L);

        Assert.assertEquals(0L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(10L, aBank.getBalance("Bob-456"));
        //
        LOGGER.log(Level.INFO, String.format("Alice's balance: %s", aBank.getBalance("Alice-123")));
        LOGGER.log(Level.INFO, String.format("Bob's balance: %s", aBank.getBalance("Bob-456")));
    }

    /**
     * https://vladmihalcea.com/race-condition/
     * @param aBank
     */
    protected void raceConditionTest(TheBank aBank) {
        aBank.newAccount("Alice-123", 10L);
        aBank.newAccount("Bob-456", 0L);

        Assert.assertEquals(10L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(0L, aBank.getBalance("Bob-456"));

        int threadCount = 8;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                awaitOnLatch(startLatch);
                aBank.transfer("Alice-123", "Bob-456", 5l);
                endLatch.countDown();
            }).start();
        }

        LOGGER.info("Starting threads");
        startLatch.countDown();
        LOGGER.info("Main thread waits for all transfer threads to finish");
        awaitOnLatch(endLatch);
        //
        LOGGER.log(Level.INFO, String.format("Alice's balance: %s", aBank.getBalance("Alice-123")));
        LOGGER.log(Level.INFO, String.format("Bob's balance: %s", aBank.getBalance("Bob-456")));
    }

    protected void raceConditionTest_Fix(TheFixBank aBank) {
        aBank.newAccount("Alice-123", 10L);
        aBank.newAccount("Bob-456", 0L);

        Assert.assertEquals(10L, aBank.getBalance("Alice-123"));
        Assert.assertEquals(0L, aBank.getBalance("Bob-456"));

        int threadCount = 8;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                awaitOnLatch(startLatch);
                aBank.transfer("Alice-123", "Bob-456", 5l);
                if (!(aBank instanceof TheFixBank)) endLatch.countDown();
            }).start();
        }

        if (aBank instanceof TheFixBank) {
            aBank.onTaskComplete((msg, state) -> {
                if (msg != null) LOGGER.info(msg.toString());
                endLatch.countDown();
            });
        }

        LOGGER.info("Starting threads");
        startLatch.countDown();
        LOGGER.info("Main thread waits for all transfer threads to finish");
        awaitOnLatch(endLatch);
        //
        LOGGER.log(Level.INFO, String.format("Alice's balance: %s", aBank.getBalance("Alice-123")));
        LOGGER.log(Level.INFO, String.format("Bob's balance: %s", aBank.getBalance("Bob-456")));
    }

    protected void awaitOnLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

}
