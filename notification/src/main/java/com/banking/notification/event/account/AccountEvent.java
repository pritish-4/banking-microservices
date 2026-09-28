package com.banking.notification.event.account;

import java.time.LocalDateTime;

public record AccountEvent(
        Long accountId,
        String accountNumber,
        Long customerId,
        String eventType,
        LocalDateTime occurredAt
) {}
