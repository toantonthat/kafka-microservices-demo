package com.scheduler;

import com.entity.TransactionLog;
import com.model.TransactionLogRequest;
import com.service.LedgerService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * This class is responsible for recovering transactions that were not processed successfully.
 */
@Component
public class RecoveryTask {

  private static final Logger logger = LoggerFactory.getLogger(RecoveryTask.class);
  private final LedgerService ledgerService;

  public RecoveryTask(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

  /*
   * Implement a recovery task in case of the system got the problem while processing the transaction
   */
  @Scheduled(fixedDelay = 60000)
  public void recoverFailedTransactions() {
    try {
      List<TransactionLog> failedLogs = ledgerService.getTransactionLogsByStatus("FAILED");
      for (TransactionLog log : failedLogs) {
        TransactionLogRequest transactionLogRequest = new TransactionLogRequest();
        transactionLogRequest.setUserId(log.getUserId());
        transactionLogRequest.setAmount(log.getAmount());
        transactionLogRequest.setTransactionType(log.getTransactionType());

        ledgerService.logTransaction(transactionLogRequest);
        ledgerService.updateTransactionLogStatus(log.getTransactionId(), "COMPLETED");
      }
    } catch (Exception e) {
      logger.error("Error recovering failed transactions: {}", e.getMessage());
    }
  }
}
