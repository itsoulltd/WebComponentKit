package com.itsoul.lab.ledgerbook.accounting.head;

import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.Transaction;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.generalledger.services.Cryptor;
import com.itsoul.lab.generalledger.util.AESCipher;
import com.itsoul.lab.ledgerbook.connector.SQLConnector;
import com.itsoul.lab.ledgerbook.connector.SourceConfig;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LedgerTest {

    ChartOfAccounts chartOfAccounts;
    SourceConnector connector;
    Ledger ledger;
    Cryptor cryptor;

    @Before
    public void setUp() throws Exception {
        connector = new SQLConnector(SourceConfig.JDBC_MYSQL)
                .url("jdbc:mysql://localhost:3316/ledgerDB")
                .username("root")
                .password("root@123")
                .skipSchemaGeneration(false);
        //Testing with Embedded DB:
        /*connector = new SQLConnector(SourceConfig.EMBEDDED_H2)
                .url("jdbc:h2:mem:ledgerDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE")
                .username("sa")
                .password("")
                .skipSchemaGeneration(false);*/
        cryptor = new AESCipher();
    }

    @After
    public void tearDown() throws Exception {
        //Comment-Out- if wish to keep table-data for doTransaction():
        if (connector != null){
            //cleanStorage();
            //dropTables();
        }
        cryptor = null;
    }

    @Test
    public void doAccounting(){
        //Clean First:
        try {
            cleanStorage();
        } catch (SQLException e) {}
        //
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .create("CASH_ACCOUNT_1", "1000.00", "EUR")
                .create("REVENUE_ACCOUNT_1", "0.00", "EUR")
                .create("CASH_ACCOUNT_2", "2000.00", "EUR")
                .build();
        //
        String secret = "ILoveYou-BD";
        //
        Ledger book = new Ledger.LedgerBuilder(chartOfAccounts)
                .name("Master-Ledger")
                .connector(connector)
                .client("FB_" + "test_user_a", "test_tenant_a")
                .secret(secret)
                .skipLogPrinting(false)
                .build();

        //Transfer request:
        String transactionRef = "T:1:" + (new Random().nextInt(9)+1);
        TransferRequest transferRequest1 = book.createTransferRequest()
                .reference(transactionRef)
                .type("testing1")
                .account("CASH_ACCOUNT_1").debit("5.00", "EUR")
                .account("REVENUE_ACCOUNT_1").credit("5.00", "EUR")
                .build();

        book.commit(transferRequest1);

        //Testing TransactionLeg's are valid:
        transferRequest1.getLegs()
                .stream()
                .forEach(leg -> {
                    boolean assertion = leg.isSignatureValid(secret, cryptor);
                    Assert.assertTrue(assertion);
                    //
                    String message = String.format("%s is valid: %s"
                            , leg.getAccountRef()
                            , assertion);
                    System.out.println(message);
                    //
                });

        //More Transactions:
        String transactionRef2 = "T:1:" + (new Random().nextInt(9)+1);
        TransferRequest transferRequest2 = book.createTransferRequest()
                .reference(transactionRef2)
                .type("testing2")
                .account("CASH_ACCOUNT_1").credit("15.00", "EUR")
                .account("CASH_ACCOUNT_2").debit("15.00", "EUR")
                .build();
        book.commit(transferRequest2);

        //Search For Account:
        List<Transaction> cashAccountTransactionList = book.findTransactions("CASH_ACCOUNT_1");
        Assert.assertTrue(cashAccountTransactionList.size() > 0);
        List<Transaction> revenueAccountTransactionList = book.findTransactions("REVENUE_ACCOUNT_1");
        Assert.assertTrue(revenueAccountTransactionList.size() > 0);

        //Search For Transaction:
        Transaction transaction1 = book.getTransactionByRef(transactionRef);
        Assert.assertTrue(transaction1 != null);
        Transaction transaction2 = book.getTransactionByRef(transactionRef2);
        Assert.assertTrue(transaction2 != null);

        //At the end close the ledger book:
        book.close();
    }

    //@Test //Run when transactions are persisted in realDB like MySQL
    public void doTransaction(){
        //
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .retrive("CASH_ACCOUNT_1")
                .retrive("REVENUE_ACCOUNT_1")
                .build();
        //
        String secret = "ILoveYou-BD";
        //
        Ledger book = new Ledger.LedgerBuilder(chartOfAccounts)
                .name("Master-Account")
                .connector(connector)
                .secret("ILoveYou-BD")
                .client("FB_test_user_a", "test_tenant_a")
                .secret(secret)
                .build();

        //Transfer request:
        TransferRequest transferRequest1 = book.createTransferRequest()
                .reference("T" + (new Random().nextInt(9)+1))
                .type("testing1")
                .account("CASH_ACCOUNT_1").debit("5.00", "EUR")
                .account("REVENUE_ACCOUNT_1").credit("5.00", "EUR")
                .build();

        book.commit(transferRequest1);

        //At the end close the ledger book:
        book.close();
    }

    //@Test
    public void searchTransactions(){
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .retrive("CASH_ACCOUNT_1")
                .retrive("REVENUE_ACCOUNT_1")
                .build();
        //
        Ledger book = new Ledger.LedgerBuilder(chartOfAccounts)
                .name("Master-Account")
                .connector(connector)
                .client("FB_test_user_a", "test_tenant_a")
                .build();

        //Search For Account:
        List<Transaction> cashAccountTransactionList = book.findTransactions("CASH_ACCOUNT_1");
        List<Transaction> revenueAccountTransactionList = book.findTransactions("REVENUE_ACCOUNT_1");

        //Search For Transaction:
        Transaction transaction1 = book.getTransactionByRef("T1");
        Transaction transaction2 = book.getTransactionByRef("T2");
    }

    //@Test
    public void stringTest(){
        Predicate p = new Where("client_ref").isEqualTo("qwert")
                        .and("tenant_ref").isEqualTo("asdf")
                        .and("transaction_ref").isIn(Arrays.asList("dhaka", "chittagong", "khulna"));

        SQLSelectQuery query = new SQLQuery.Builder(QueryType.SELECT)
                .columns("transaction_ref", "transaction_type", "transaction_date")
                .from("transaction_history")
                .where(p)
                .build();
        System.out.println(query.toString());
        System.out.println(query.bindValueToString());
    }

    //@Test
    public void cleanStorage() throws SQLException {
        SQLExecutor executor = (SQLExecutor) connector.getExecutor();
        //
        SQLDeleteQuery deleteQuery = new SQLQuery.Builder(QueryType.DELETE)
                .rowsFrom("client").where(new Where("ref").notNull()).build();
        executor.executeDelete(deleteQuery.bindValueToString());
        //
        deleteQuery = new SQLQuery.Builder(QueryType.DELETE)
                .rowsFrom("account").where(new Where("client_ref").notNull()).build();
        executor.executeDelete(deleteQuery);
        //
        deleteQuery = new SQLQuery.Builder(QueryType.DELETE)
                .rowsFrom("transaction_history").where(new Where("client_ref").notNull()).build();
        executor.executeDelete(deleteQuery.bindValueToString());
        //
        deleteQuery = new SQLQuery.Builder(QueryType.DELETE)
                .rowsFrom("transaction_leg").where(new Where("client_ref").notNull()).build();
        executor.executeDelete(deleteQuery);
        //

    }

    //@Test
    public void dropTables() throws SQLException {
        SQLExecutor executor = (SQLExecutor) connector.getExecutor();
        executor.executeDDLQuery("drop table client;");
        executor.executeDDLQuery("drop table account;");
        executor.executeDDLQuery("drop table transaction_history;");
        executor.executeDDLQuery("drop table transaction_leg;");
    }
}