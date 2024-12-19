package com.repository;

import com.entity.Balance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {
  Optional<Balance> findByUserId(String userId);
}
