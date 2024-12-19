package com.controller;

import com.model.BalanceRequest;
import com.service.BalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {
  private final BalanceService balanceService;

  public BalanceController(BalanceService balanceService) {
    this.balanceService = balanceService;
  }

  @GetMapping("/{userId}/is-negative")
  public ResponseEntity<Boolean> isBalanceNegative(@PathVariable String userId) {
    boolean isNegative = balanceService.isBalanceNegative(userId);
    return ResponseEntity.ok(isNegative);
  }

  @PostMapping("/process")
  public ResponseEntity<Void> updateBalance(@RequestBody BalanceRequest balanceRequest) {
    balanceService.updateBalance(balanceRequest);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
