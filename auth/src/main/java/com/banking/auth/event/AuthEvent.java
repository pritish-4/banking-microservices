package com.banking.auth.event;

import java.time.LocalDateTime;

public record AuthEvent(
        Long userId,
        String username,
        String eventType,
        LocalDateTime occurredAt
) {}
