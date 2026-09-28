package com.example.banking.repo;

import com.example.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

    @Query("select t from Transaction t where t.from.id = :accountId OR t.to.id = :accountId")
    List<Transaction> findAllByAccountId(@Param("accountId") int accountId);

}
