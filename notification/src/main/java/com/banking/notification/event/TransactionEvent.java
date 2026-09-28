package com.banking.notification.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String transactionId,
        String type,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}
