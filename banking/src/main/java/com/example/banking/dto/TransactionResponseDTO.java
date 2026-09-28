package com.example.banking.dto;

import java.time.LocalDateTime;

public record TransactionResponseDTO(
        int id,
        int fromId,
        int toId,
        double amount,
        LocalDateTime time
) { }
