package com.itsoul.lab.generalledger.exception;

/**
 * Business exception thrown if a referenced transfer request already exists.
 *
 *
 */
public class TransferRequestExistsException extends BusinessException {

  private static final long serialVersionUID = -6114857738235916873L;

  public TransferRequestExistsException(String transactionRef) {
    super("Transfer request already exists for reference '" + transactionRef + "'");
  }
}
