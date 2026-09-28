package com.banking.account.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        String accountNumber,
        BigDecimal balance
) {}

