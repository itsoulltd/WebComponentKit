package com.itsoul.lab.application.test;

import com.it.soul.lab.connect.DriverClass;
import com.itsoul.lab.application.bank.SCBank;
import com.itsoul.lab.application.bank.SCFixBank;
import com.itsoul.lab.application.bank.TheBank;
import com.itsoul.lab.application.bank.TheFixBank;
import org.junit.Test;

import static com.itsoul.lab.application.bank.TheBank.executeScript;

public class TheBankTest extends BaseBankTest{

    @Test
    public void leanerTest() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheBank aBank = new SCBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        singleThreadTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTest() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheBank aBank = new SCBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        raceConditionTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTest_Fix() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.H2_EMBEDDED);
        executeScript("db/h2-schema.sql", DriverClass.H2_EMBEDDED);
        TheFixBank aBank = new SCFixBank(DriverClass.H2_EMBEDDED, "anatolia", "324123");
        raceConditionTest_Fix(aBank);
        aBank.close();
    }

}
