package com.banking.transaction.controller;

import com.banking.transaction.dto.DepositRequest;
import com.banking.transaction.dto.TransactionResponse;
import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.dto.WithdrawRequest;
import com.banking.transaction.entity.TransactionType;
import com.banking.transaction.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            HttpServletRequest httpRequest) {
        log.info("POST /transactions/deposit - accountId: {}", request.accountId());
        return ResponseEntity.ok(service.deposit(request, httpRequest.getHeader("Authorization")));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            HttpServletRequest httpRequest) {
        log.info("POST /transactions/withdraw - accountId: {}", request.accountId());
        return ResponseEntity.ok(service.withdraw(request, httpRequest.getHeader("Authorization")));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            HttpServletRequest httpRequest) {
        log.info("POST /transactions/transfer - from: {}, to: {}", request.fromAccountId(), request.toAccountId());
        return ResponseEntity.ok(service.transfer(request, httpRequest.getHeader("Authorization")));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @RequestParam(required = false) TransactionType type,
            HttpServletRequest httpRequest) {
        log.info("GET /transactions/my - type: {}", type);
        String authHeader = httpRequest.getHeader("Authorization");
        if (type != null) {
            return ResponseEntity.ok(service.getMyTransactionsByType(type, authHeader));
        }
        return ResponseEntity.ok(service.getMyTransactions(authHeader));
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> getByTransactionId(@PathVariable String transactionId) {
        log.info("GET /transactions/{}", transactionId);
        return ResponseEntity.ok(service.getByTransactionId(transactionId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        log.info("GET /transactions - admin fetching all");
        return ResponseEntity.ok(service.getAllTransactions());
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable Long accountId) {
        log.info("GET /transactions/account/{} - admin fetching by account", accountId);
        return ResponseEntity.ok(service.getTransactionsByAccount(accountId));
    }
}
