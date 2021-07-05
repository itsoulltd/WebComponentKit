package com.itsoul.lab.generalledger.validation;

import com.itsoul.lab.generalledger.entities.TransactionLeg;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.generalledger.exception.AccountNotFoundException;
import com.itsoul.lab.generalledger.exception.InsufficientFundsException;
import com.itsoul.lab.generalledger.exception.UnbalancedLegsException;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author towhid
 * @since 19-Aug-19
 */
@Contract
public interface TransferValidator {

  void validateTransferRequest(TransferRequest transferRequest);

  void transferRequestExists(String transactionRef);

  void isTransactionBalanced(Iterable<TransactionLeg> legs) throws UnbalancedLegsException;

  void currenciesMatch(Iterable<TransactionLeg> legs)
      throws TransferValidationException, AccountNotFoundException;

  void validBalance(Iterable<TransactionLeg> legs) throws InsufficientFundsException;

}
