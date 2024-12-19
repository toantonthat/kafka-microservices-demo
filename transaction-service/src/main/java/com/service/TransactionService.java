package com.service;

import com.client.BalanceClient;
import com.client.LedgerClient;
import com.entity.Transaction;
import com.enums.TransactionLogStatus;
import com.exception.TransactionServiceException;
import com.model.BalanceRequest;
import com.model.TransactionEvent;
import com.model.TransactionLogRequest;
import com.model.TransactionType;
import com.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepository transactionRepository;
  private final BalanceClient balanceClient;
  private final LedgerClient ledgerClient;

  public TransactionService(
      TransactionRepository transactionRepository,
      BalanceClient balanceClient,
      LedgerClient ledgerClient
  ) {
    this.transactionRepository = transactionRepository;
    this.balanceClient = balanceClient;
    this.ledgerClient = ledgerClient;
  }

  @Async
  @Transactional
  public void processTransaction(TransactionEvent transactionEvent) {
    String transactionId = UUID.randomUUID().toString();
    TransactionLogStatus transactionStatus = TransactionLogStatus.COMPLETED;

    try {
      // Check if the user's balance will be negative after the transaction
      ResponseEntity<Boolean> balanceResponse = balanceClient.isBalanceNegative(
          transactionEvent.getUserId());

      if (balanceResponse.getBody() != null && balanceResponse.getBody()) {
        logTransactionInLedgerService(transactionEvent, transactionId,
            TransactionLogStatus.PENDING);
        throw new TransactionServiceException(
            "Transaction failed: User's balance will be negative.");
      }

      // Store transaction in the database
      saveTransaction(transactionEvent, transactionId);

      // Update user's balance
      updateBalanceInBalanceService(transactionEvent);
    } catch (Exception e) {
      transactionStatus = TransactionLogStatus.FAILED;
      logger.error("Error processing transaction: {}", e.getMessage());
    } finally {
      // Log the transaction in LedgerService using Feign client
      logTransactionInLedgerService(transactionEvent, transactionId, transactionStatus);
    }
  }

  private void saveTransaction(
      TransactionEvent transactionEvent,
      String transactionId
  ) {
    Transaction transaction = new Transaction();
    transaction.setId(transactionId);
    transaction.setUserId(transactionEvent.getUserId());
    transaction.setAmount(BigDecimal.valueOf(transactionEvent.getAmount()));
    transaction.setTransactionType(transactionEvent.getType().name());

    transactionRepository.save(transaction);
  }

  private void logTransactionInLedgerService(
      TransactionEvent transactionEvent,
      String transactionId,
      TransactionLogStatus status
  ) {
    TransactionLogRequest logRequest = new TransactionLogRequest();
    logRequest.setUserId(transactionEvent.getUserId());
    logRequest.setAmount(BigDecimal.valueOf(transactionEvent.getAmount()));
    logRequest.setTransactionId(transactionId);
    logRequest.setStatus(status.name());
    logRequest.setTransactionType(transactionEvent.getType().name());

    logRequest.setMetadata("Transaction logged from TransactionService");
    ledgerClient.logTransaction(logRequest);
  }

  private void updateBalanceInBalanceService(TransactionEvent transactionEvent) {
    BalanceRequest balanceRequest = new BalanceRequest();
    balanceRequest.setUserId(transactionEvent.getUserId());
    balanceRequest.setAmount(BigDecimal.valueOf(transactionEvent.getAmount()));
    balanceRequest.setType(TransactionType.valueOf(transactionEvent.getType().name()));
    balanceClient.updateBalance(balanceRequest);
  }
}
