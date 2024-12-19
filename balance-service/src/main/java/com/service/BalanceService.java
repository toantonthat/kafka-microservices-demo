package com.service;

import com.entity.Balance;
import com.exception.BalanceServiceException;
import com.model.BalanceRequest;
import com.repository.BalanceRepository;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {
  private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
  private final BalanceRepository balanceRepository;
  private final ConcurrentHashMap<String, Balance> balanceMap = new ConcurrentHashMap<>();

  public BalanceService(BalanceRepository balanceRepository) {
    this.balanceRepository = balanceRepository;
  }

  public Balance getBalance(String userId) {
    return balanceMap.computeIfAbsent(userId, id -> {
      // If not present in memory, retrieve from the database
      return balanceRepository.findByUserId(userId)
          .orElseGet(() -> createNewBalance(userId));
    });
  }

  @Transactional
  public void updateBalance(BalanceRequest balanceRequest) {
    Balance balance = getBalance(balanceRequest.getUserId());

    synchronized (balance) {
      try {

        BigDecimal newBalance = calculateNewBalance(balance.getAmount(), balanceRequest);
        boolean isNotExisted = Objects.isNull(balance.getId());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
          logger.error("Balance is negative for user: {}", balanceRequest.getUserId());
          throw new BalanceServiceException(
              "Balance is negative for user: " + balanceRequest.getUserId());
        }

        if (newBalance.compareTo(BigDecimal.ZERO) == 0 && isNotExisted) {
          logger.error("Balance is zero for unavailable user: {}", balanceRequest.getUserId());
          throw new BalanceServiceException(
              "Balance is zero for unavailable user: " + balanceRequest.getUserId());
        }

        if (isNotExisted) {
          balance.setId(UUID.randomUUID().toString());
        }

        balance.setAmount(newBalance);
        balanceRepository.save(balance);
        balanceMap.put(balanceRequest.getUserId(), balance);
        logger.info("Balance updated successfully for user: {}", balanceRequest.getUserId());
      } catch (Exception e) {
        logger.error("Error updating balance: {}", e.getMessage());
        throw new BalanceServiceException("Error updating balance");
      }
    }
  }

  public boolean isBalanceNegative(String userId) {
    Balance balance = getBalance(userId);
    return balance.getAmount().compareTo(BigDecimal.ZERO) < 0;
  }

  private BigDecimal calculateNewBalance(BigDecimal currentBalance, BalanceRequest balanceRequest) {
    switch (balanceRequest.getType()) {
      case DEPOSIT:
        return currentBalance.add(balanceRequest.getAmount());
      case WITHDRAWAL:
        return currentBalance.subtract(balanceRequest.getAmount());
      default:
        throw new BalanceServiceException(
            "Unsupported transaction type: " + balanceRequest.getType());
    }
  }

  private Balance createNewBalance(String userId) {
    Balance balance = new Balance();
    balance.setUserId(userId);
    balance.setAmount(BigDecimal.ZERO);
    return balance;
  }
}
