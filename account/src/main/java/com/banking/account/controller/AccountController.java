package com.banking.account.controller;

import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AdminAccountResponse;
import com.banking.account.dto.BalanceResponse;
import com.banking.account.dto.CreateAccountRequest;
import com.banking.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest req) {

        return ResponseEntity.ok(service.createAccount(req));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        return ResponseEntity.ok(service.getMyAccounts());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminAccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(service.getAllAccounts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminAccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAccount(id));
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getBalance(accountNumber));
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<AdminAccountResponse> getAccountInternal(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAccountInternal(id));
    }

    @PutMapping("/internal/{id}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable Long id,
            @RequestBody java.math.BigDecimal newBalance) {
        service.updateBalance(id, newBalance);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountResponse> getMyAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getMyAccountByNumber(accountNumber));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> closeAccount(@PathVariable Long id) {
        service.closeAccount(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> freezeAccount(@PathVariable Long id) {
        service.freezeAccount(id);
        return ResponseEntity.ok().build();
    }
}
