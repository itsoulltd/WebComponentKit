package com.itsoul.lab.ledgerbook.repositories;

import com.google.common.collect.Lists;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;
import com.it.soul.lab.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.entities.Transaction;
import com.itsoul.lab.generalledger.entities.TransactionLeg;
import com.itsoul.lab.generalledger.entities.mapper.RowMapper;
import com.itsoul.lab.generalledger.repositories.TransactionRepository;
import com.itsoul.lab.generalledger.services.Cryptor;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The Data Object Access support to abstract and encapsulate all access to the transaction_history
 * and transaction_leg tables of the data source.
 *
 */
@Service
public class TransactionRepositoryImpl implements TransactionRepository {

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private QueryExecutor queryExecutor;
    private Client clientRef;

    @Inject
    private Cryptor cryptor;

    private SQLExecutor getExecutor(){
        if (queryExecutor instanceof SQLExecutor)
            return (SQLExecutor) queryExecutor;
        return null;
    }

    private Integer[] storeTransactionLegs(List<TransactionLeg> legs, Client clientRef, String transactionRef) throws SQLException {
        //
        Row row = new Row()
                .add("client_ref")
                .add("tenant_ref")
                .add("transaction_ref")
                .add("account_ref")
                .add("entry")
                .add("amount")
                .add("currency")
                .add("balance")
                .add("eventTimestamp")
                .add("signature");
        SQLInsertQuery insertQuery = new SQLQuery.Builder(QueryType.INSERT)
                .into("transaction_leg")
                .values(row.getProperties().toArray(new Property[0]))
                .build();
        List<Row> rowsToInsert = new ArrayList<>();
        legs.forEach(leg -> {
            Row rowToInsert = new Row()
                    .add("client_ref", getClientRef().getRef())
                    .add("tenant_ref", getClientRef().getTenantRef())
                    .add("transaction_ref", transactionRef)
                    .add("account_ref", leg.getAccountRef())
                    .add("entry", leg.getEntry())
                    .add("amount", leg.getAmount().getAmount().doubleValue())
                    .add("currency", leg.getAmount().getCurrency().getCurrencyCode())
                    .add("balance", leg.getBalance().doubleValue())
                    .add("eventTimestamp", leg.encryptedTimestamp(getClientRef().getSecret(), cryptor))
                    .add("signature", leg.getSignature());
            rowsToInsert.add(rowToInsert);
        });
        //
        Integer[] results = getExecutor().executeInsert(false, legs.size(), insertQuery, rowsToInsert);
        //
        if (results.length > 0)
            log.info("TransactionLegs are successfully stored.");
        else
            log.warning("TransactionLegs are failed to store!");
        //
        return results;
    }

    @Override
    public void storeTransaction(Transaction transaction) throws SQLException{
        //
        Row row = new Row().add("client_ref",getClientRef().getRef())
                .add("tenant_ref",getClientRef().getTenantRef())
                .add("transaction_ref", transaction.getTransactionRef())
                .add("transaction_type", transaction.getTransactionType())
                .add("transaction_date", new java.sql.Date(transaction.getTransactionDate().getTime()));
        SQLInsertQuery query = new SQLQuery.Builder(QueryType.INSERT)
                .into("transaction_history")
                .values(row.getProperties().toArray(new Property[0]))
                .build();
        //
        int result = getExecutor().executeInsert(false, query);
        storeTransactionLegs(transaction.getLegs(), getClientRef(), transaction.getTransactionRef());
        if (result > 0) {
            log.info("Transaction created: " + transaction.getTransactionRef());
        }
        else log.warning("Transaction Creation Failed: " + transaction.getTransactionRef());
        //
    }

    @Override
    public Set<String> getTransactionRefsForAccount(String accountRef) {
        //
        SQLSelectQuery query = new SQLQuery.Builder(QueryType.SELECT)
                .columns("transaction_ref")
                .from("transaction_leg")
                .where(new Where("account_ref").isEqualTo(accountRef))
                .build();
        //
        try {
            List<Row> rows = getExecutor()
                    .convertToLists(getExecutor().executeSelect(query), "transaction_ref");
            Set<String> transaction_refs = rows.stream()
                    .map(row -> row.keyObjectMap().get("transaction_ref").toString())
                    .collect(Collectors.toSet());
            return transaction_refs;
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactions(List<String> transactionRefs) {
        //
        Predicate searchFor = new Where("client_ref").isEqualTo(getClientRef().getRef())
                .and("tenant_ref").isEqualTo(getClientRef().getTenantRef())
                .and("transaction_ref").isIn(transactionRefs);
        //
        SQLSelectQuery query = new SQLQuery.Builder(QueryType.SELECT)
                .columns("transaction_ref", "transaction_type", "transaction_date")
                .from("transaction_history")
                .where(searchFor)
                .build();
        try {
            return new TransactionMapper().extract(getExecutor().executeSelect(query.bindValueToString()));
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public Transaction getTransactionByRef(String transactionRef) {
        List<String> transactionRefList = Lists.newArrayList();
        transactionRefList.add(transactionRef);
        List<Transaction> transactionsList = getTransactions(transactionRefList);
        if (transactionsList.isEmpty()) {
            return null;
        }
        return transactionsList.get(0);
    }

    private List<TransactionLeg> getLegsForTransaction(String transactionRef) {
        //
        Predicate searchFor = new Where("client_ref").isEqualTo(getClientRef().getRef())
                .and("tenant_ref").isEqualTo(getClientRef().getTenantRef())
                .and("transaction_ref").isEqualTo(transactionRef);
        //
        SQLSelectQuery query = new SQLQuery.Builder(QueryType.SELECT)
                .columns("account_ref", "entry", "amount", "currency", "balance", "eventTimestamp", "signature")
                .from("transaction_leg")
                .where(searchFor)
                .build();
        try {
            return new TransactionLegMapper().extract(getExecutor().executeSelect(query));
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public void setClientRef(Client clientRef) {
        this.clientRef = clientRef;
    }

    @Override
    public Client getClientRef() {
        return this.clientRef;
    }

    public void setQueryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public QueryExecutor getQueryExecutor() {
        return this.queryExecutor;
    }

    private class TransactionMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet rs, int rowNum, int columnCount) throws SQLException {
            String transactionRef = rs.getString("transaction_ref");
            String transactionType = rs.getString("transaction_type");
            Date transactionDate = new Date(rs.getDate("transaction_date").getTime());
            List<TransactionLeg> legs = getLegsForTransaction(transactionRef);
            return new Transaction(transactionRef, transactionType, transactionDate, legs);
        }
    }

    private static class TransactionLegMapper implements RowMapper<TransactionLeg> {

        @Override
        public TransactionLeg mapRow(ResultSet rs, int rowNum, int columnCount) throws SQLException {
            String accountRef = rs.getString("account_ref");
            String entry = rs.getString("entry");
            BigDecimal amount = new BigDecimal(rs.getString("amount"));
            Currency currency = Currency.getInstance(rs.getString("currency"));
            Money transferredFunds = new Money(amount, currency);
            TransactionLeg leg = new TransactionLeg(accountRef, transferredFunds, entry);
            leg.setBalance(new BigDecimal(rs.getString("balance")));
            leg.setEventTimestamp(rs.getString("eventTimestamp"));
            leg.setSignature(rs.getString("signature"));
            return leg;
        }
    }

}
