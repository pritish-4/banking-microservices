package com.banking.account.dto;

import com.banking.account.entity.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotNull
        AccountType accountType,

        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal initialBalance
) {}

