package com.service;

import com.model.LedgerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LedgerSubscribe {

  private static final Logger logger = LoggerFactory.getLogger(LedgerSubscribe.class);
  private final LedgerService ledgerService;

  public LedgerSubscribe(LedgerService ledgerService) {
    this.ledgerService = ledgerService;
  }

//  @KafkaListener(topics = "transaction-topic", groupId = "ledger-service")
//  public void handleTransactionLogEvent(LedgerEvent transactionEvent) {
//    try {
//      logger.info("Received transaction event: {}", transactionEvent);
//
////      logTransaction(transactionEvent);
////
////      // Here you would typically call another service to process the transaction
////      // For example, BalanceService.process(transactionEvent);
//
//      // On success, update the log status
//      ledgerService.updateTransactionLogStatus(transactionEvent.getTransactionId(), "COMPLETED");
//    } catch (Exception e) {
//      // On error, log it and handle recovery
//      ledgerService.updateTransactionLogStatus(transactionEvent.getTransactionId(), "FAILED");
//
//      logger.error("Error processing transaction event: {}", transactionEvent, e);
//    }
//  }
}
