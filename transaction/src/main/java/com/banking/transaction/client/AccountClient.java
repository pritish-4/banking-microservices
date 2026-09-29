package com.banking.transaction.client;

import com.banking.transaction.dto.AccountBalanceResponse;
import com.banking.transaction.dto.CustomerAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/accounts/my")
    List<CustomerAccountResponse> getMyAccounts(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/accounts/internal/{id}")
    AccountBalanceResponse getAccount(@PathVariable Long id,
                                      @RequestHeader("Authorization") String authHeader);

    @PutMapping("/accounts/internal/{id}/balance")
    void updateBalance(@PathVariable Long id,
                       @RequestBody BigDecimal newBalance,
                       @RequestHeader("Authorization") String authHeader);
}
