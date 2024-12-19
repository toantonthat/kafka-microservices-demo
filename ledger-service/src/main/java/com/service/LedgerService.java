package com.service;

import com.entity.TransactionLog;
import com.exception.LedgerServiceException;
import com.model.TransactionLogRequest;
import com.repository.LedgerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {

  private static final Logger logger = LoggerFactory.getLogger(LedgerService.class);
  private final LedgerRepository ledgerRepository;

  public LedgerService(LedgerRepository ledgerRepository) {
    this.ledgerRepository = ledgerRepository;
  }

  public List<TransactionLog> getTransactionLogsByStatus(String status) {
    return ledgerRepository.findAllByStatus(status);
  }

  @Transactional
  public void logTransaction(TransactionLogRequest transactionLogRequest) {
    try {
      TransactionLog transactionLog = new TransactionLog();
      transactionLog.setId(UUID.randomUUID().toString());
      transactionLog.setTransactionId(transactionLogRequest.getTransactionId());
      transactionLog.setUserId(transactionLogRequest.getUserId());
      transactionLog.setAmount(transactionLogRequest.getAmount());
      transactionLog.setTransactionType(transactionLogRequest.getTransactionType());
      transactionLog.setStatus(transactionLogRequest.getStatus());
      transactionLog.setTimestamp(LocalDateTime.now());

      ledgerRepository.save(transactionLog);
      logger.info("Transaction logged successfully: {}", transactionLog.getId());
    } catch (Exception e) {
      logger.error("Error logging transaction: {}", e.getMessage());
      throw new LedgerServiceException("Error logging transaction");
    }
  }

  @Transactional
  public void updateTransactionLogStatus(String transactionId, String status) {
    TransactionLog log = ledgerRepository.findById(transactionId)
        .orElseThrow(() -> new LedgerServiceException("Transaction log not found"));
    log.setStatus(status);
    ledgerRepository.save(log);
  }

  public List<TransactionLog> getTransactionHistory(String userId) {
    return ledgerRepository.findByUserId(userId);
  }
}
