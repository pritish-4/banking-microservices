package com.banking.transaction.dto;

import com.banking.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionId,
        TransactionType type,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}
