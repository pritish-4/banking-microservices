package com.banking.transaction.repo;

import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.id = :id")
    Optional<Transaction> findByIdWithLock(@Param("id") Long id);

    List<Transaction> findBySourceAccountIdOrTargetAccountId(Long sourceId, Long targetId);

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findBySourceAccountIdOrTargetAccountIdAndType(Long sourceId, Long targetId, TransactionType type);
}
