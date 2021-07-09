Description
--------------------

**Double-entry bookkeeping** involves making at least two entries or legs for every transaction.
A debit in one account and a corresponding credit in another account.
The sum of all debits should always equal the sum of all credits, providing a simple way to check for errors.
The following rules **MUST** apply:

  * An account **MUST NOT** be overdrawn, i.e. have a negative balance.
  * A monetary transaction **MAY** support multiple currencies as long as the total balance for the transaction legs with the same currency is zero.
  * The concepts of debit and credit are simplified by specifying that monetary transactions towards an account can have either a positive or negative value.
  
 Inspired By 
 https://github.com/imetaxas/double-entry-bookkeeping-api
 
 API
 ----
        SourceConnector connector = new SQLConnector(SourceConfig.JDBC_MYSQL)
                        .url("jdbc:mysql://localhost:3316/ledgerDB")
                        .username("my-ac-user")
                        .password("******")
                        .skipSchemaGeneration(false);
         
        Cryptor cryptor = new AESCipher();
        
        ChartOfAccounts chartOfAccounts = new ChartOfAccounts.ChartOfAccountsBuilder()
                        .create("CASH_ACCOUNT_1", "1000.00", "EUR")
                        .create("REVENUE_ACCOUNT", "0.00", "EUR")
                        .create("CASH_ACCOUNT_2", "2000.00", "EUR")
                        .build();
                        
        String secret = "xyzwqerfrt54123dfdfawe-12lklkerSSqere";
         
        Ledger book = new Ledger.LedgerBuilder(chartOfAccounts)
                        .name("Master-Ledger")
                        .connector(connector)
                        .client("FB_" + "test_user_a", "test_tenant_a")
                        .secret(secret)
                        .skipLogPrinting(false)
                        .build();
                        
        //Transfer request:
        String transactionRef = "my-sales-transaction";
        TransferRequest transferRequest1 = book.createTransferRequest()
                .reference(transactionRef)
                .type("SALES-REVENUE")
                .account("CASH_ACCOUNT_1").debit("500.00", "EUR")
                .account("REVENUE_ACCOUNT").credit("500.00", "EUR")
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
                
        //Search all Transactions By Account-Title:
        List<Transaction> cashAccountTransactionList = book.findTransactions("CASH_ACCOUNT_1");
        //Search a Transaction By transaction-reference no:
        Transaction transaction1 = book.getTransactionByRef(transactionRef);
        
        //At the end close the ledger book:
        book.close();
        
 End
 ---