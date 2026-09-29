package com.banking.transaction.dto;

import java.math.BigDecimal;

public record CustomerAccountResponse(
        Long accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String status
) {}
