package com.itsoul.lab.ledgerbook.repositories;

import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.*;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Row;
import com.it.soul.lab.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.Account;
import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.entities.TransactionLeg;
import com.itsoul.lab.generalledger.entities.mapper.EntityMapper;
import com.itsoul.lab.generalledger.repositories.AccountRepository;
import org.jvnet.hk2.annotations.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Data Object Access support to abstract and encapsulate all access to the account table of the
 * data source.
 *
 */
@Service
public class AccountRepositoryImpl implements AccountRepository {

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private QueryExecutor queryExecutor;
    private Client clientRef;

    private SQLExecutor getExecutor(){
        if (queryExecutor instanceof SQLExecutor)
            return (SQLExecutor) queryExecutor;
        return null;
    }

    @Override
    public boolean accountExists(String accountRef) {
        //
        SQLScalarQuery query = new SQLQuery.Builder(QueryType.COUNT)
                .columns("id")
                .on("account")
                .where(new Where("account_ref").isEqualTo(accountRef)
                        .and("client_ref").isEqualTo(getClientRef().getRef())
                        .and("tenant_ref").isEqualTo(getClientRef().getTenantRef()))
                .build();
        //
        try {
            int count = getExecutor().getScalarValue(query);
            return count > 0;
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return false;
    }

    @Override
    public void createAccount(Client client, String accountRef, Money initialAmount) {
        //
        Row row = new Row().add("client_ref",client.getRef())
                .add("tenant_ref",client.getTenantRef())
                .add("account_ref", accountRef)
                .add("currency", initialAmount.getCurrency().getCurrencyCode())
                .add("amount", initialAmount.getAmount().doubleValue());
        //
        SQLInsertQuery insertQuery = new SQLQuery.Builder(QueryType.INSERT)
                .into("account")
                .values(row.getProperties().toArray(new Property[0]))
                .build();
        try {
            int result = getExecutor().executeInsert(true, insertQuery);
            //
            if(result > 0) log.info("Account Created: " + accountRef);
            else log.warning("Account Creating Failed: " + accountRef);
            //
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    @Override
    public Account getAccount(String accountRef) {
        //
        SQLSelectQuery query = new SQLQuery.Builder(QueryType.SELECT)
                .columns("account_ref","amount","currency")
                .from("account")
                .where(new Where("account_ref").isEqualTo(accountRef)
                        .and("client_ref").isEqualTo(getClientRef().getRef())
                        .and("tenant_ref").isEqualTo(getClientRef().getTenantRef()))
                .build();
        try {
            ResultSet rs = getExecutor().executeSelect(query);
            return new AccountMapper().mapEntity(rs);
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return Account.nullAccount();
    }

    @Override
    public void updateBalance(TransactionLeg leg) throws SQLException{
        //
        double newBalance = currentBalance(leg.getAccountRef()) + leg.getAmount().getAmount().doubleValue();
        SQLUpdateQuery query = new SQLQuery.Builder(QueryType.UPDATE)
                .set(new Property("amount", newBalance))
                .from("account")
                .where(new Where("account_ref").isEqualTo(leg.getAccountRef())
                        .and("client_ref").isEqualTo(getClientRef().getRef())
                        .and("tenant_ref").isEqualTo(getClientRef().getTenantRef()))
                .build();
        //
        int result = getExecutor().executeUpdate(query);
        //
        if(result > 0) log.info("Account Updated: " + leg.getAccountRef());
        else log.warning("Account Update Failed: " + leg.getAccountRef());
        //
    }

    private double currentBalance(String accountRef){
        Account account = getAccount(accountRef);
        return account.getBalance().getAmount().doubleValue();
    }

    public void setQueryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public QueryExecutor getQueryExecutor() {
        return this.queryExecutor;
    }

    @Override
    public Client getClientRef() {
        return clientRef;
    }

    @Override
    public void setClientRef(Client clientRef) {
        this.clientRef = clientRef;
    }

    private static class AccountMapper implements EntityMapper<Account> {

        @Override
        public Account mapRow(ResultSet rs, int numRow, int columnCount) throws SQLException {
            String accountRef = rs.getString("account_ref");
            BigDecimal amount = new BigDecimal(rs.getString("amount"));
            Currency currency = Currency.getInstance(rs.getString("currency"));
            Money balance = new Money(amount, currency);
            return new Account(accountRef, balance);
        }
    }

}