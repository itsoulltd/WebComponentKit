package com.itsoul.lab.generalledger.services;

import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.exception.AccountNotFoundException;
import org.jvnet.hk2.annotations.Contract;

/**
 * Defines the business logic for managing monetary accounts.
 *
 *
 */
@Contract
public interface AccountService {

  /**
   * Create a new account with an initial balance.
   *
   * @param accountRef a client defined account reference
   * @param amount the initial account balance
   */
  void createAccount(String accountRef, Money amount);

  /**
   * Get the current balance for a given account.
   *
   * @param accountRef the client defined account reference
   * @return the account balance
   * @throws AccountNotFoundException if the referenced account does not exist
   */
  Money getAccountBalance(String accountRef) throws AccountNotFoundException;
}
