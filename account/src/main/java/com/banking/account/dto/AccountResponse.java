package com.banking.account.dto;

import com.banking.account.entity.AccountStatus;
import com.banking.account.entity.AccountType;

import java.math.BigDecimal;

public record AccountResponse(
        Long accountId,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance,
        AccountStatus status
) {}

