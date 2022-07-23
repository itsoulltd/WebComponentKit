package com.itsoul.lab.ledgerbook.accounting.head;

import com.itsoul.lab.generalledger.entities.*;
import com.itsoul.lab.generalledger.exception.InfrastructureException;
import com.itsoul.lab.generalledger.exception.LedgerAccountException;
import com.itsoul.lab.ledgerbook.connector.SQLConnector;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A general ledger contains all the accounts for recording transactions relating to assets,
 * liabilities, owners' equity, revenue, and expenses.
 *
 * @author towhidul islam
 * @since 16-Sept-19
 */
public class Ledger extends AbstractAccountingConcept {

    private static final String FACTORY_CLASS_NAME = "com.itsoul.lab.ledgerbook.accounting.dependency.ContextResolverImpl";

    private String name;
    private ChartOfAccounts chartOfAccounts;
    private boolean skipPrinting;

    private Ledger(LedgerBuilder builder) throws InfrastructureException {
        init(FACTORY_CLASS_NAME, builder.connector, builder.client);
        this.chartOfAccounts = builder.chartOfAccounts;
        this.name = builder.name;
        this.skipPrinting = builder.skipPrinting;
    }

    private Ledger init() {
        chartOfAccounts.get().forEach(account -> {
            if (!account.getBalance().isNullMoney()) {
                getAccountService().createAccount(account.getAccountRef(), account.getBalance());
            }
        });
        return this;
    }

    public void commit(TransferRequest transferRequest) {
        validateAccountRefs(
                transferRequest.getLegs()
                        .stream()
                        .map(TransactionLeg::getAccountRef)
                        .toArray(String[]::new)
        );
        getTransferService().transferFunds(transferRequest);
    }

    private void validateAccountRefs(String... accountRefs) {
        List<String> chartOfAccountsRefs = this.chartOfAccounts.get()
                .stream()
                .map(Account::getAccountRef)
                .collect(Collectors.toList());

        for (String ref : accountRefs) {
            if (!chartOfAccountsRefs.contains(ref)) {
                throw new LedgerAccountException(ref);
            }
        }
    }

    public TransferRequest.ReferenceStep createTransferRequest() {
        return TransferRequest.builder();
    }

    public List<Transaction> findTransactions(String accountRef) {
        validateAccountRefs(accountRef);
        return getTransferService().findTransactionsByAccountRef(accountRef);
    }

    public Transaction getTransactionByRef(String transactionRef) {
        return getTransferService().getTransactionByRef(transactionRef);
    }

    public Money getAccountBalance(String accountRef) {
        validateAccountRefs(accountRef);
        return getAccountService().getAccountBalance(accountRef);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public void close() {
        printHistoryLog();
        super.close();
    }

    public void printHistoryLog() {
        if (skipPrinting) return;
        try {
            String logInfo = formatAccounts() + "\n\n" + formatTransactionLog() + "\n\n";
            LOG.log(Level.INFO, logInfo);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private String formatAccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ledger: " + name + "\n\n");
        sb.append(String
                .format("%20s %20s %10s %15s %10s %10s", "Account", "|", "Amount", "|", "Currency", "|"));
        sb.append(String.format("%s",
                "\n------------------------------------------------------------------------------------------"));

        chartOfAccounts.get().forEach(account -> {
            Money money = getAccountService().getAccountBalance(account.getAccountRef());
            sb.append("\n" + String
                    .format("%20s %20s %10.2f %15s %10s %10s", account.getAccountRef(), "|",
                            money.getAmount(), "|",
                            money.getCurrency(), "|"));
        });

        return sb.toString();
    }

    private String formatTransactionLog() {
        StringBuilder sb = new StringBuilder();

        sb.append(String
                .format("%20s %20s %15s %10s %10s %10s %10s", "Account", "|", "Transaction", "|", "Type",
                        "|", "Date"));
        sb.append(String.format("%s",
                "\n-------------------------------------------------------------------------------------------------------------------------"));

        chartOfAccounts.get().forEach(account -> {
            List<Transaction> transactions = getTransferService()
                    .findTransactionsByAccountRef(account.getAccountRef());
            if (!transactions.isEmpty()) {
                transactions.forEach(transaction -> sb.append("\n" + String
                        .format("%20s %20s %10s %15s %10s %10s %10s %1s", account.getAccountRef(), "|",
                                transaction.getTransactionRef(), "|", transaction.getTransactionType(), "|",
                                transaction.getTransactionDate(), "|")));
            } else {
                sb.append("\n" + String
                        .format("%20s %20s %10s %15s %10s %10s %10s %1s", account.getAccountRef(), "|",
                                "N/A", "|", "N/A", "|",
                                "N/A", "|"));
            }
        });
        chartOfAccounts.get().forEach(account -> {
            List<Transaction> transactions = getTransferService()
                    .findTransactionsByAccountRef(account.getAccountRef());
            if (!transactions.isEmpty()) {
                sb.append(
                        "\n\n" + String.format("%20s %20s %15s %4s %10s %10s %15s %5s %10s %10s", "Account", "|",
                                "Transaction Leg Ref", "|", "Amount", "|", "Currency", "|", "Balance", "|"));
                sb.append(String.format("%s",
                        "\n-------------------------------------------------------------------------------------------------------------------------"));
                transactions.forEach(transaction ->
                        transaction.getLegs().forEach(leg -> sb.append("\n" + String
                                .format("%20s %20s %10s %10s %10s %10s %10s %10s %10s %10s",
                                        account.getAccountRef(), "|",
                                        leg.getAccountRef(), "|",
                                        leg.getAmount().getAmount(), "|",
                                        leg.getAmount().getCurrency(), "|",
                                        leg.getBalance(), "|"))));
            }
        });

        return sb.toString();
    }

    public static class LedgerBuilder {

        private String name = "General Ledger";
        private ChartOfAccounts chartOfAccounts;
        private SourceConnector connector = SQLConnector.EMBEDDED_H2_CONNECTOR;
        private Client client = new Client("Client_" + System.currentTimeMillis()
                , "Tenant_" + System.currentTimeMillis());
        private boolean skipPrinting = true;

        public LedgerBuilder(ChartOfAccounts chartOfAccounts) {
            this.chartOfAccounts = chartOfAccounts;
        }

        public LedgerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LedgerBuilder connector(SourceConnector connector) {
            this.connector = connector;
            return this;
        }

        public LedgerBuilder client(String ref, String tenantRef){
            String secret = this.client.getSecret();
            this.client = new Client(ref, tenantRef);
            return secret(secret);
        }

        public LedgerBuilder secret(String secret){
            this.client.setSecret(secret);
            return this;
        }

        public LedgerBuilder skipLogPrinting(boolean skipPrinting){
            this.skipPrinting = skipPrinting;
            return this;
        }

        public Ledger build() {
            return new Ledger(this).init();
        }
    }
}
