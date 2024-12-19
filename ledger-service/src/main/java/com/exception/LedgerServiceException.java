package com.exception;

public class LedgerServiceException extends RuntimeException {
  public LedgerServiceException(String message) {
    super(message);
  }

  public LedgerServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
