package com.banking.transaction.event;

import com.banking.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String transactionId,
        TransactionType type,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}
