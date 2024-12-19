package com.controller;

import com.model.TransactionLogRequest;
import com.service.LedgerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

  private final LedgerService ledgerService;

  public LedgerController(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

  @PostMapping("/log")
  public ResponseEntity<Void> recordTransaction(@RequestBody TransactionLogRequest transactionLog) {
    ledgerService.logTransaction(transactionLog);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/history/{userId}")
  public ResponseEntity<?> getTransactionHistory(@PathVariable String userId) {
    try {
      return ResponseEntity.ok(ledgerService.getTransactionHistory(userId));
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to retrieve transaction history: " + e.getMessage());
    }
  }
}
