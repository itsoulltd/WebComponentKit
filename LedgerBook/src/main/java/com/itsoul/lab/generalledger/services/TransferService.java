package com.itsoul.lab.generalledger.services;

import com.itsoul.lab.generalledger.entities.Transaction;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.generalledger.exception.*;
import org.jvnet.hk2.annotations.Contract;

import java.util.List;

/**
 * Defines the business logic for performing monetary transactions (account transfers) between
 * accounts.
 *
 *
 */
@Contract
public interface TransferService {

  /**
   * Executes a balanced, multi-legged monetary transaction as a single unit of work.
   *
   * @param transferRequest a transfer request describing the transactions legs
   * @throws IllegalArgumentException If the entries are less than two or other key properties are
   * missing
   * @throws AccountNotFoundException if a specified account does not exist
   * @throws InsufficientFundsException if a participating account is overdrawn
   * @throws UnbalancedLegsException if the transaction legs are unbalanced
   * @throws InfrastructureException on non-recoverable infrastructure errors
   */
  void transferFunds(TransferRequest transferRequest);

  /**
   * Finds all monetary transactions performed towards a given account.
   *
   * @param accountRef the client defined account reference to find transactions for
   * @return list of transactions or an empty list if none is found
   * @throws AccountNotFoundException if the specified account does not exist
   * @throws InfrastructureException on non-recoverable infrastructure errors
   */
  List<Transaction> findTransactionsByAccountRef(String accountRef);

  /**
   * Get a given transaction by reference.
   *
   * @param transactionRef the transaction reference
   * @return the transaction or null if it doesnt exist
   * @throws InfrastructureException on non-recoverable infrastructure errors
   */
  Transaction getTransactionByRef(String transactionRef);
}
