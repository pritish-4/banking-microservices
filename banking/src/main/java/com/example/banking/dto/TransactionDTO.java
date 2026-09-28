package com.example.banking.dto;

import jakarta.validation.constraints.NotNull;

public record TransactionDTO(
        @NotNull(message = "Account ID is required")
        int fromId,

        @NotNull(message = "Account ID is required")
        int toId,

        @NotNull(message = "amount cannot be null")
        double amount

) { }
