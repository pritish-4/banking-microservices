package com.banking.transaction.dto;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        Long id,
        String accountNumber,
        Long customerId,
        BigDecimal balance
) {}
