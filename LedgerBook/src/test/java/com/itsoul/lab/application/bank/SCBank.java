package com.itsoul.lab.application.bank;

import com.infoworks.connect.JDBCDriverClass;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SCBank implements TheBank {

    private static Logger LOG = Logger.getLogger(SCBank.class.getSimpleName());
    private JDBCDriverClass driverClass;
    private String user;
    private String password;

    public SCBank(JDBCDriverClass driverClass, String manager, String password) {
        this.driverClass = driverClass;
        this.user = manager == null ? "manager" : manager;
        this.password = password == null ? "man@123" : password;
        newAccount("Master", 1000000);
    }

    private LedgerBook ledgerBook;

    protected final LedgerBook getLedgerBook() {
        if (ledgerBook == null){
            SourceConnector connector = TheBank.createSourceConnector(driverClass);
            LedgerBook ledgerBook = new LedgerBook(connector, user, password, "SCBank", "BDT");
            this.ledgerBook = ledgerBook;
        }
        return ledgerBook;
    }

    protected final Money addBalanceFromMaster(LedgerBook ledgerBook, String iban, long balance) {
        Money money = addBalance(ledgerBook, "Master", iban, balance);
        return money;
    }

    private Money addBalance(LedgerBook ledgerBook, String fromIban, String toIban, long transferAmount) {
        //TODO: transferAmount has to be 0.00 or any combination with at least 2 digit after precision.
        // e.g. 1002001.00 or 1200933.97 etc
        BigDecimal transferMoney = BigDecimal.valueOf(transferAmount);
        transferMoney = transferMoney.setScale(2, RoundingMode.UP);
        Money money = ledgerBook.makeTransactions(
                "transfer", UUID.randomUUID().toString().substring(0, 20)
                , String.format("%s@%s", getPrefix(), fromIban)
                , transferMoney.toPlainString()
                , String.format("%s@%s", getPrefix(), toIban)
        );
        return money;
    }

    protected String getPrefix() {
        return "CH";
    }

    @Override
    public void newAccount(String iban, long balance) {
        if (iban == null || iban.isEmpty()) return;
        if (balance < 0L) return;
        LedgerBook ledgerBook = getLedgerBook();
        String possibleACNo = LedgerBook.getACNo(iban, getPrefix());
        Money money = ledgerBook.createAccount(getPrefix(), iban, String.valueOf(balance));
        LOG.log(Level.INFO, String.format("%s is created with balance: %s %s", possibleACNo, money.getAmount().longValue(), money.getCurrency()));
    }

    @Override
    public void addBalance(String iban, long balance) {
        if (iban == null || iban.isEmpty()) return;
        LedgerBook ledgerBook = getLedgerBook();
        addBalanceFromMaster(ledgerBook, iban, balance);
    }

    @Override
    public long getBalance(String iban) {
        if (iban == null || iban.isEmpty()) return 0L;
        LedgerBook ledgerBook = getLedgerBook();
        Money money = ledgerBook.readBalance(getPrefix(), iban);
        return money.getAmount().longValue();
    }

    @Override
    public void transfer(String fromIban, String toIban, long transferAmount) {
        if (fromIban == null || fromIban.isEmpty()) return;
        if (toIban == null || toIban.isEmpty()) return;
        if (transferAmount <= 0L) return;
        LedgerBook ledgerBook = getLedgerBook();
        long fromBalance = ledgerBook.readBalance(getPrefix(), fromIban).getAmount().longValue();
        if(fromBalance >= transferAmount) {
            addBalance(ledgerBook, fromIban, toIban, transferAmount);
        }
        if (fromBalance >= transferAmount)
            LOG.log(Level.INFO, String.format("Transfer: %s -> %s : %s", fromIban, toIban, transferAmount));
        else
            LOG.log(Level.INFO, String.format("Insufficient Fund: %s : %s", fromIban, fromBalance));
    }

}
