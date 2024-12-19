package com.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

  @Id
  private String id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String transactionType; // e.g., deposit or withdrawal

  public Transaction() {
  }

  public Transaction(String userId, BigDecimal amount, String transactionType) {
    this.userId = userId;
    this.amount = amount;
    this.transactionType = transactionType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }
}
