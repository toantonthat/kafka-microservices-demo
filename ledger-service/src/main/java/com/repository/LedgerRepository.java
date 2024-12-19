package com.repository;

import com.entity.TransactionLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<TransactionLog, String> {

  List<TransactionLog> findByUserId(String userId);

  List<TransactionLog> findAllByStatus(String status);
}
