package com.itsoul.lab.generalledger.validation;

import com.itsoul.lab.generalledger.exception.BusinessException;

/**
 * Business exception thrown if a transfer validation is illegal.
 *
 *
 */
public class TransferValidationException extends BusinessException {

  private static final long serialVersionUID = -6114850538235916873L;

  public TransferValidationException(String message) {
    super(message);
  }
}
