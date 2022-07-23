package com.itsoul.lab.generalledger.repositories;

import com.itsoul.lab.generalledger.entities.Account;
import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.entities.TransactionLeg;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface AccountRepository extends Repository{
  boolean accountExists(String accountRef);
  void createAccount(Client clientRef, String accountRef, Money initialAmount);
  Account getAccount(String accountRef);
  double updateBalance(TransactionLeg leg) throws SQLException;
  void setClientRef(Client clientRef);
  Client getClientRef();
}
