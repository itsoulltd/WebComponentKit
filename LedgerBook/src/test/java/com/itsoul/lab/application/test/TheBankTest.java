package com.itsoul.lab.application.test;

import com.it.soul.lab.connect.DriverClass;
import com.itsoul.lab.application.bank.SCBank;
import com.itsoul.lab.application.bank.SCFixBank;
import com.itsoul.lab.application.bank.TheBank;
import com.itsoul.lab.application.bank.TheFixBank;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.itsoul.lab.application.bank.TheBank.executeScript;

public class TheBankTest {

    private static Logger LOGGER = Logger.getLogger(TheBankTest.class.getSimpleName());

    @Test
    public void leanerTestWithH2DBAuthDB() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheBank aBank = new SCBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        singleThreadTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTestWithH2DBAuthDB() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheBank aBank = new SCBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        raceConditionTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTestWithH2DBAuthDB_Fix() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheFixBank aBank = new SCFixBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        raceConditionTest_Fix(aBank);
        aBank.close();
    }

    private void singleThreadTest(TheBank aBank) {
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
    private void raceConditionTest(TheBank aBank) {
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

    private void raceConditionTest_Fix(TheFixBank aBank) {
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

    private void awaitOnLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

}
