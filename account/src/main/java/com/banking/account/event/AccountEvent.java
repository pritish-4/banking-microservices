package com.banking.account.event;

import java.time.LocalDateTime;

public record AccountEvent(
        Long accountId,
        String accountNumber,
        Long customerId,
        String eventType,
        LocalDateTime occurredAt
) {}
