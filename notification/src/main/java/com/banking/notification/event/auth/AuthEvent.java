package com.banking.notification.event.auth;

import java.time.LocalDateTime;

public record AuthEvent(
        Long userId,
        String username,
        String eventType,
        LocalDateTime occurredAt
) {}
