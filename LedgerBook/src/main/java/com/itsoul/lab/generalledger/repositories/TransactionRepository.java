package com.itsoul.lab.generalledger.repositories;

import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.entities.Transaction;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Contract
public interface TransactionRepository extends Repository{
  void storeTransaction(Transaction transaction) throws SQLException;
  Set<String> getTransactionRefsForAccount(String accountRef);
  List<Transaction> getTransactions(List<String> transactionRefs);
  Transaction getTransactionByRef(String transactionRef);
  void setClientRef(Client clientRef);
  Client getClientRef();
}
