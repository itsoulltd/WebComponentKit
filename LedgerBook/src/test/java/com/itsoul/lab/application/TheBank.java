package com.itsoul.lab.application;

import com.it.soul.lab.connect.DriverClass;
import com.it.soul.lab.connect.JDBConnection;
import com.it.soul.lab.connect.io.ScriptRunner;
import com.itsoul.lab.ledgerbook.connector.SQLConnector;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public interface TheBank extends AutoCloseable{
    long getBalance(String iban);
    void addBalance(String iban, long balance);
    void newAccount(String iban, long balance);
    void transfer(String fromIban, String toIban, long transferAmount);

    @Override
    default void close() throws Exception {}

    static Connection createConnection(DriverClass driver) throws SQLException {
        Connection connection;
        switch (driver) {
            case MYSQL:
                connection = new JDBConnection.Builder(DriverClass.MYSQL)
                        .host("localhost", "3306")
                        .database("testDB")
                        .credential("root", "root@123")
                        .build();
                break;
            case OracleOCI9i:
                connection = new JDBConnection.Builder(DriverClass.OracleOCI9i)
                        .host("localhost", "1521")
                        .database("xe")
                        .credential("system", "oracle")
                        .build();
                break;
            default:
                connection = new JDBConnection.Builder(driver)
                        .database("testDB")
                        .credential("sa", "sa")
                        .query(";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_LOWER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE")
                        .build();
        }
        return connection;
    }

    static SourceConnector createSourceConnector(DriverClass driver) {
        SourceConnector connection;
        switch (driver) {
            case MYSQL:
                String url = driver.urlSchema() + "localhost:3306" + "/testDB" + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
                connection = new SQLConnector(driver.toString())
                        .url(url)
                        .username("root")
                        .password("root@123")
                        .schema(driver.urlSchema())
                        .skipSchemaGeneration(true);
                break;
            case OracleOCI9i:
                url = driver.urlSchema() + "localhost:1521" + "/xe";
                connection = new SQLConnector(driver.toString())
                        .url(url)
                        .username("system")
                        .password("oracle")
                        .schema(driver.urlSchema())
                        .skipSchemaGeneration(true);
                break;
            default:
                url = driver.urlSchema() + "testDB" + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
                connection = new SQLConnector(driver.toString())
                        .url(url)
                        .username("sa")
                        .password("sa")
                        .schema(driver.urlSchema())
                        .skipSchemaGeneration(true);
        }
        return connection;
    }

    static void executeScript(String initSqlFileName, DriverClass driver) throws SQLException {
        Connection connection = TheBank.createConnection(driver);
        ScriptRunner runner = new ScriptRunner();
        File file = new File(initSqlFileName);
        String[] cmds = runner.commands(runner.createStream(file));
        runner.execute(cmds, connection);
    }

}
