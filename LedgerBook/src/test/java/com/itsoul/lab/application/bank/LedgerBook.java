package com.itsoul.lab.application.bank;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLJoinQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.*;
import com.itsoul.lab.generalledger.util.AESCipher;
import com.itsoul.lab.ledgerbook.accounting.head.ChartOfAccounts;
import com.itsoul.lab.ledgerbook.accounting.head.Ledger;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
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
            book = initLedger(chartOfAccounts);
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
            book = initLedger(chartOfAccounts);
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
            book = initLedger(chartOfAccounts);
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
            , String to) throws RuntimeException {
        return makeTransactions(type, ref, AccountingType.Liability, from, amount, to);
    }

    public Money makeTransactions(String type
            , String ref
            , AccountingType acType
            , String from
            , String amount
            , String to) throws RuntimeException {
        //
        Ledger book = null;
        try {
            //
            ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                    .retrive(from)
                    .retrive(to)
                    .build();
            //
            book = initLedger(chartOfAccounts);

            //Transfer request:
            TransferRequest transferRequest1 = book.createTransferRequest()
                    .reference(validateTransactionRef(ref))
                    .type(type, acType)
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

    private Ledger initLedger(ChartOfAccounts chartOfAccounts){
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
        Ledger book = initLedger(chartOfAccounts);
        List<Transaction> cashAccountTransactionList = book.findTransactions(cash_account);
        return cashAccountTransactionList;
    }

    public Map<String, String> convertTransaction(Transaction transaction, String matcher) {
        Map<String, String> info = new HashMap<>();
        info.put("transaction-ref", transaction.getTransactionRef());
        Optional<TransactionLeg> tLeg = transaction.getLegs().stream()
                .filter(leg -> leg.getAccountRef().equalsIgnoreCase(matcher))
                .findFirst();
        if (tLeg.isPresent()){
            info.put("amount", tLeg.get().getAmount().getAmount().toPlainString());
            info.put("currency", tLeg.get().getAmount().getCurrency().getCurrencyCode());
            info.put("currencyDisplayName", tLeg.get().getAmount().getCurrency().getDisplayName());
            info.put("balance", tLeg.get().getBalance().toPlainString());
        }
        info.put("transaction-type", transaction.getTransactionType());
        /*info.put("transaction-date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(transaction.getTransactionDate()));*/
        info.put("transaction-date", transaction.getTransactionDate().getTime() + "");
        return info;
    }

    /**
     * Select
     *    th.transaction_ref
     *  , th.transaction_type
     *  , th.transaction_date
     *  , tl.account_ref
     *  , tl.amount
     *  , tl.currency
     *  , tl.balance, tl.eventtimestamp
     * from transaction_history as th
     * Left Join transaction_leg as tl on (th.transaction_ref =  tl.transaction_ref)
     * where 1
     * and tl.account_ref = 'CASH@towhid'
     * and tl.tenant_ref = 'my-app-id'
     * and tl.client_ref = 'sa'
     * order by th.transaction_date DESC;
     */

    public List<Map<String, Object>> findTransactions(String prefix, String username, SearchQuery query) {
        String cash_account = getACNo(username, prefix);
        Predicate clause = new Where("tl.account_ref").isEqualTo(cash_account);
        //Queryable- by Type, From-Date, To-Date
        if (query.get("type") != null){
            clause.and("th.transaction_type").isLike("%"+ query.get("type", String.class)+"%");
        }
        if (query.get("from") != null && query.get("to") != null) {
            String from = query.get("from", String.class);
            String to = minusADay(query.get("to", String.class), "yyyy-MM-dd");
            clause.and("th.transaction_date").between(from, to);
        } else if (query.get("from") != null && query.get("till") != null) {
            String from = query.get("from", String.class);
            String till = query.get("till", String.class);
            clause.and("th.transaction_date").between(from, till);
        } else {
            if (query.get("from") != null) {
                clause.and("th.transaction_date").isGreaterThenOrEqual(query.get("from", String.class));
            } else if (query.get("to") != null) {
                clause.and("th.transaction_date").isLessThen(query.get("to", String.class));
            } else if (query.get("till") != null) {
                clause.and("th.transaction_date").isLessThenOrEqual(query.get("till", String.class));
            }
        }
        //
        int limit = query.getSize() <= 0 ? 10 : query.getSize();
        int offset = query.getPage() <= 0 ? 0 : ((query.getPage() - 1) * limit);
        SQLJoinQuery joins = new SQLQuery.Builder(QueryType.LEFT_JOIN)
                .joinAsAlice("transaction_history", "th"
                        , "transaction_ref", "transaction_type", "transaction_date")
                .on("transaction_ref", "transaction_ref")
                .joinAsAlice("transaction_leg", "tl"
                        , "account_ref", "amount", "currency", "balance", "eventtimestamp")
                .where(clause)
                .orderBy(Operator.DESC, "th.transaction_date")
                .addLimit(limit, offset)
                .build();
        //
        LOG.info(joins.toString());
        try (SQLExecutor executor = new SQLExecutor(connector.getConnection())) {
            ResultSet set = executor.executeSelect(joins);
            List<Map<String, Object>> data = executor.convertToKeyValuePair(set);
            //A-Hack: get the actual transaction-datetime from encrypted eventTimestamp:
            AESCipher cipher = new AESCipher();
            data.forEach(row -> {
                String val = Optional.ofNullable(row.remove("eventtimestamp")).orElse("").toString();
                if (!val.isEmpty()) {
                    try {
                        String decrypt = cipher.decrypt(password, val);
                        long timestamp = Long.parseLong(decrypt);
                        row.put("transaction_date", LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC));
                    } catch (Exception e) { LOG.log(Level.WARNING, e.getMessage()); }
                }
            });
            //
            return data;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
        return new ArrayList<>();
    }

    private String minusADay(String date, String dateFormat) {
        if (Objects.isNull(date) || date.isEmpty()) dateFormat = "yyyy-MM-dd";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            cal.setTime(format.parse(date));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            date = format.format(cal.getTime());
        } catch (ParseException e) {}
        return date;
    }

}
