package com.banking.account.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long accountId,
        String accountNumber,
        BigDecimal balance
) {}

