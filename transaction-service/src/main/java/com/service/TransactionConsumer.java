package com.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {
  private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
  private final TransactionService transactionService;

  public TransactionConsumer(
      TransactionService transactionService
  ) {
    this.transactionService = transactionService;
  }

  @KafkaListener(topics = "transaction-topic", groupId = "transaction-service")
  public void consumeMessages(String message) {
    try {
      logger.info("Received message: {}", message);
      ObjectMapper objectMapper = new ObjectMapper();
      TransactionEvent transactionEvent = objectMapper.readValue(message, TransactionEvent.class);
      transactionService.processTransaction(transactionEvent);
      logger.info("Transaction saved: {}", transactionEvent);
    } catch (Exception e) {
      logger.error("Error processing transaction: {}", e.getMessage());
    }
  }
}
