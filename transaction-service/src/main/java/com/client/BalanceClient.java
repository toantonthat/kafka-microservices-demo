package com.client;

import com.model.BalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "balance-service", url = "${services.balance.url}")
public interface BalanceClient {
  @GetMapping("/{userId}/is-negative")
  ResponseEntity<Boolean> isBalanceNegative(@PathVariable("userId") String userId);

  @PostMapping("/process")
  ResponseEntity<Void> updateBalance(@RequestBody BalanceRequest balanceRequest);
}
