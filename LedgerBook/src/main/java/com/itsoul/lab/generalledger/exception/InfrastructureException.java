package com.itsoul.lab.generalledger.exception;

/**
 * Exception thrown on usually unrecoverable infrastructure exception.
 *
 *
 */
public class InfrastructureException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InfrastructureException(String message) {
    super(message);
  }

  public InfrastructureException(Throwable cause) {
    super(cause);
  }
}
