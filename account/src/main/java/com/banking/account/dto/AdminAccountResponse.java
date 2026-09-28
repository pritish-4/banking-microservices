package com.banking.account.dto;

import com.banking.account.entity.AccountStatus;
import com.banking.account.entity.AccountType;

import java.math.BigDecimal;

public record AdminAccountResponse(
        Long accountId,
        String accountNumber,
        Long customerId,
        AccountType accountType,
        BigDecimal balance,
        AccountStatus status
) {}
