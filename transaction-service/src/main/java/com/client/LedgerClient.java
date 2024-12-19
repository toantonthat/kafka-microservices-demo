package com.client;

import com.model.TransactionLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ledger-service", url = "${services.ledger.url}")
public interface LedgerClient {

  @PostMapping("/log")
  HttpStatus logTransaction(@RequestBody TransactionLogRequest transactionLogRequest);
}
