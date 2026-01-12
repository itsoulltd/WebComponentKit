package com.itsoul.lab.application.test;

import com.infoworks.connect.JDBCDriverClass;
import com.itsoul.lab.application.bank.SCBank;
import com.itsoul.lab.application.bank.SCFixBank;
import com.itsoul.lab.application.bank.TheBank;
import com.itsoul.lab.application.bank.TheFixBank;
import org.junit.Test;

import static com.itsoul.lab.application.bank.TheBank.executeScript;

public class TheBankTestMySQL extends BaseBankTest{

    @Test
    public void leanerTest() throws Exception {
        executeScript("db/drop-all-tables.sql", JDBCDriverClass.MYSQL);
        executeScript("db/mysql-schema.sql", JDBCDriverClass.MYSQL);
        TheBank aBank = new SCBank(JDBCDriverClass.MYSQL, "anatolia", "324123");
        singleThreadTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTest() throws Exception {
        executeScript("db/drop-all-tables.sql", JDBCDriverClass.MYSQL);
        executeScript("db/mysql-schema.sql", JDBCDriverClass.MYSQL);
        TheBank aBank = new SCBank(JDBCDriverClass.MYSQL, "anatolia", "324123");
        raceConditionTest(aBank);
        aBank.close();
    }

    @Test
    public void concurrentTest_Fix() throws Exception {
        executeScript("db/drop-all-tables.sql", JDBCDriverClass.MYSQL);
        executeScript("db/mysql-schema.sql", JDBCDriverClass.MYSQL);
        TheFixBank aBank = new SCFixBank(JDBCDriverClass.MYSQL, "anatolia", "324123");
        raceConditionTest_Fix(aBank);
        aBank.close();
    }

}
