package com.itsoul.lab.application.test;

import com.it.soul.lab.connect.DriverClass;
import com.itsoul.lab.application.bank.SCBank;
import com.itsoul.lab.application.bank.SCFixBank;
import com.itsoul.lab.application.bank.TheBank;
import com.itsoul.lab.application.bank.TheFixBank;
import org.junit.Test;

import static com.itsoul.lab.application.bank.TheBank.executeScript;

public class TheBankTestOracle extends BaseBankTest{

    @Test
    public void leanerTestWithH2DBAuthDB() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.OracleOCI9i);
        executeScript("db/oracle-schema.sql", DriverClass.OracleOCI9i);
        TheBank aBank = new SCBank(DriverClass.OracleOCI9i, "anatolia", "324123");
        singleThreadTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTestWithH2DBAuthDB() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.OracleOCI9i);
        executeScript("db/oracle-schema.sql", DriverClass.OracleOCI9i);
        TheBank aBank = new SCBank(DriverClass.OracleOCI9i, "anatolia", "324123");
        raceConditionTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTestWithH2DBAuthDB_Fix() throws Exception {
        executeScript("db/drop-all-tables.sql", DriverClass.OracleOCI9i);
        executeScript("db/oracle-schema.sql", DriverClass.OracleOCI9i);
        TheFixBank aBank = new SCFixBank(DriverClass.OracleOCI9i, "anatolia", "324123");
        raceConditionTest_Fix(aBank);
        aBank.close();
    }

}
