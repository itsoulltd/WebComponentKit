package com.itsoul.lab.generalledger.exception;

/**
 * Business exception thrown if a referenced account does not exist.
 *
 *
 */
public class AccountNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  public AccountNotFoundException(String accountRef) {
    super("No account found for reference '" + accountRef + "'");
  }
}
