package com.example.banking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AccountDTO(
        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotNull(message = "Balance cannot be null")
        double balance
) { }
