package com.itsoul.lab.application;

import com.itsoul.lab.domain.models.TransactionSearchQuery;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLJoinQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.entities.Transaction;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.ledgerbook.accounting.head.ChartOfAccounts;
import com.itsoul.lab.ledgerbook.accounting.head.Ledger;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This a thread-safe bean:
 */
public class LedgerBook {

    private static Logger LOG = Logger.getLogger(LedgerBook.class.getSimpleName());
    private SourceConnector connector;
    private String owner;
    private String password;
    private String tenantID;
    private String currency;

    public LedgerBook(SourceConnector connector, String owner, String password, String tenantID, String currency) {
        this.connector = connector;
        this.owner = owner;
        this.password = password;
        this.tenantID = tenantID;
        this.currency = currency;
    }

    public Money createAccount(String prefix, String username, String deposit)
            throws RuntimeException{
        //
        String cash_account = getACNo(username, prefix);
        //
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .create(cash_account, deposit, currency)
                .build();
        //
        Money money = null;
        Ledger book = null;
        try {
            book = getLedger(chartOfAccounts);
            money = Money.toMoney(deposit, currency);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (book != null)
                book.close();
        }
        return money;
    }

    public String validateTransactionRef(String ref){
        if (ref.length() > 20) ref = ref.substring(0, 19);
        return ref;
    }

    public Money readBalance(String prefix, String username) {
        //
        String cash_account = getACNo(username, prefix);
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .retrive(cash_account)
                .build();
        //
        Money money = null;
        Ledger book = null;
        try {
            book = getLedger(chartOfAccounts);
            money = book.getAccountBalance(cash_account);
        } catch (Exception e) {
            money = Money.NULL_MONEY;
        } finally {
            if (book != null)
                book.close();
        }
        return money;
    }

    public boolean isAccountExist(String prefix, String username) {
        //
        String cash_account = getACNo(username, prefix);
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .retrive(cash_account)
                .build();
        Ledger book = null;
        boolean isExist = false;
        try{
            book = getLedger(chartOfAccounts);
            isExist = book.getAccountBalance(cash_account) != null;
        }catch (Exception e) {} finally {
            if (book != null)
                book.close();
        }
        return isExist;
    }

    public Money makeTransactions(String type
            , String ref
            , String from
            , String amount
            , String to) throws RuntimeException{
        //
        Ledger book = null;
        try {
            //
            ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                    .retrive(from)
                    .retrive(to)
                    .build();
            //
            book = getLedger(chartOfAccounts);

            //Transfer request:
            TransferRequest transferRequest1 = book.createTransferRequest()
                    .reference(validateTransactionRef(ref))
                    .type(type)
                    .account(from).debit(amount, currency)
                    .account(to).credit(amount, currency)
                    .build();

            book.commit(transferRequest1);
            //At the end close the ledger book:
            Money money = book.getAccountBalance(from);
            return money;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }finally {
            if (book != null)
                book.close();
        }
    }

    private Ledger getLedger(ChartOfAccounts chartOfAccounts){
        Ledger book = new Ledger.LedgerBuilder(chartOfAccounts)
                .name("General Ledger")
                .connector(getConnector())
                .client(owner, tenantID)
                .secret(password)
                .skipLogPrinting(true)
                .build();
        return book;
    }

    public SourceConnector getConnector() {
        return connector;
    }

    public static String getName(String username){
        String name = username.split("@")[0];
        if(name.length() > 14) name = name.substring(0, 13); //14-char as Name: +8801712645571 or any unique-name length of 14 char
        return name;
    }

    public static String getACTitle(String username){
        String name = username.split("@")[0];
        if (name.length() > 20) name = name.substring(0,19);
        return name;
    }

    public static String getACNo(String username, String prefix){
        String name = getName(username);
        int nameLength = name.length();
        int howShort = Math.abs(nameLength - 14);
        if((howShort + 5) < prefix.length()) prefix = prefix.substring(0, (howShort + 5)); //greater then 5 char
        return prefix + "@" + name;
    }

    public static String parseUsername(String accountNo){
        try {
            String name = accountNo.split("@")[1];
            return name;
        } catch (Exception e) {}
        return "";
    }

    public static String parsePrefix(String accountNo){
        String prefix = accountNo.split("@")[0];
        return prefix;
    }

    public static String generateTransactionRef(String username) {
        return UUID.randomUUID().toString().substring(0, 18);
    }

    public List<Transaction> findTransactions(String prefix, String username) {
        String cash_account = getACNo(username, prefix);
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                .retrive(cash_account)
                .build();
        Ledger book = getLedger(chartOfAccounts);
        List<Transaction> cashAccountTransactionList = book.findTransactions(cash_account);
        return cashAccountTransactionList;
    }

    /**
     * Select
     *    th.transaction_ref
     *  , th.transaction_type
     *  , th.transaction_date
     *  , tl.account_ref
     *  , tl.amount
     *  , tl.currency
     *  , tl.balance
     * from transaction_history as th
     * Left Join transaction_leg as tl on (th.transaction_ref =  tl.transaction_ref)
     * where 1
     * and tl.account_ref = 'CASH@towhid'
     * and tl.tenant_ref = 'my-app-id'
     * and tl.client_ref = 'sa'
     * order by th.transaction_date DESC;
     */

    public List<Map<String, Object>> findTransactions(String prefix, String username, TransactionSearchQuery query) {
        String cash_account = getACNo(username, prefix);
        Predicate clause = new Where("tl.account_ref").isEqualTo(cash_account)
                .and("tl.tenant_ref").isEqualTo(tenantID)
                .and("tl.client_ref").isEqualTo(owner);
        //Queryable- by Type, From-Date, To-Date
        if (query.get("type") != null){
            clause.and("th.transaction_type").isLike("%"+ query.get("type", String.class)+"%");
        }
        if (query.get("from") != null && query.get("to") != null) {
            //TODO: clause.between(query.get("from", String.class), query.get("to", String.class));
        } else if (query.get("from") != null && query.get("till") != null) {
            //TODO: clause.between(query.get("from", String.class), query.get("till", String.class));
        } else {
            if (query.get("from") != null) {
                clause.and("th.transaction_date").isGreaterThenOrEqual(query.get("from", String.class));
            }
            if (query.get("to") != null) {
                clause.and("th.transaction_date").isLessThenOrEqual(query.get("to", String.class));
            }
            if (query.get("till") != null) {
                clause.and("th.transaction_date").isLessThen(query.get("till", String.class));
            }
        }
        //
        SQLJoinQuery joins = new SQLQuery.Builder(QueryType.LEFT_JOIN)
                .joinAsAlice("transaction_history", "th"
                        , "transaction_ref", "transaction_type", "transaction_date")
                .on("transaction_ref", "transaction_ref")
                .joinAsAlice("transaction_leg", "tl"
                        , "account_ref", "amount", "currency", "balance")
                .where(clause)
                .orderBy(Operator.DESC, "th.transaction_date")
                .build();
        //joins.toString();
        try (SQLExecutor executor = new SQLExecutor(connector.getConnection())) {
            ResultSet set = executor.executeSelect(joins);
            List<Map<String, Object>> data = executor.convertToKeyValuePair(set);
            return data;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
        return new ArrayList<>();
    }
}
