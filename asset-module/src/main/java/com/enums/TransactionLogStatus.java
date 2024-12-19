package com.enums;

public enum TransactionLogStatus {
  PENDING("Pending"),
  COMPLETED("Completed"),
  FAILED("Failed");

  private final String displayName;

  TransactionLogStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return displayName;
  }
}
