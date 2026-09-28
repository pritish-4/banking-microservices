package com.banking.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(

        @NotNull
        Long accountId,

        @DecimalMin(value = "0.01")
        BigDecimal amount
) {}
