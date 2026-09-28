package com.banking.account.service;

import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AdminAccountResponse;
import com.banking.account.dto.BalanceResponse;
import com.banking.account.dto.CreateAccountRequest;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.event.AccountEvent;
import com.banking.account.event.AccountEventPublisher;
import com.banking.account.exception.AccountNotFoundException;
import com.banking.account.exception.UnauthorisedAccessException;
import com.banking.account.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepo repo;
    private final AccountEventPublisher eventPublisher;

    public AccountResponse createAccount(CreateAccountRequest req) {
        Long customerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Creating account for customerId: {}, type: {}", customerId, req.accountType());

        Account acc = new Account();
        acc.setAccountNumber(generateAccountNumber());
        acc.setCustomerId(customerId);
        acc.setAccountType(req.accountType());
        acc.setBalance(req.initialBalance());
        repo.save(acc);

        log.info("Account created: {} for customerId: {}", acc.getAccountNumber(), customerId);
        return mapToResponse(acc);
    }

    public AdminAccountResponse getAccount(Long id) {
        log.info("Fetching account by id: {}", id);
        Account acc = repo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return new AdminAccountResponse(acc.getId(), acc.getAccountNumber(),
                acc.getCustomerId(), acc.getAccountType(), acc.getBalance(), acc.getStatus());
    }

    public List<AccountResponse> getMyAccounts() {
        Long customerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching accounts for customerId: {}", customerId);
        return repo.findByCustomerId(customerId).stream()
                .filter(acc -> acc.getStatus() != AccountStatus.CLOSED)
                .map(this::mapToResponse)
                .toList();
    }

    public BalanceResponse getBalance(String accountNumber) {
        log.info("Fetching balance for account: {}", accountNumber);
        Account acc = repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Long customerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!acc.getCustomerId().equals(customerId)) {
                log.warn("Unauthorized balance access - customerId: {} tried to access account: {}", customerId, accountNumber);
                throw new UnauthorisedAccessException("Unauthorized access to account");
            }
        }

        return new BalanceResponse(acc.getAccountNumber(), acc.getBalance());
    }

    public List<AdminAccountResponse> getAllAccounts() {
        log.info("Admin fetching all accounts");
        return repo.findAll().stream()
                .map(acc -> new AdminAccountResponse(acc.getId(), acc.getAccountNumber(),
                        acc.getCustomerId(), acc.getAccountType(), acc.getBalance(), acc.getStatus()))
                .toList();
    }

    public AdminAccountResponse getAccountInternal(Long id) {
        log.info("Internal fetch for accountId: {}", id);
        Account acc = repo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
        if (acc.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Transaction attempted on inactive accountId: {}", id);
            throw new AccountNotFoundException("Account is " + acc.getStatus().name().toLowerCase() + ": " + id);
        }
        return new AdminAccountResponse(acc.getId(), acc.getAccountNumber(),
                acc.getCustomerId(), acc.getAccountType(), acc.getBalance(), acc.getStatus());
    }

    public void updateBalance(Long id, BigDecimal newBalance) {
        log.info("Updating balance for accountId: {} to: {}", id, newBalance);
        Account acc = repo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
        acc.setBalance(newBalance);
        repo.save(acc);
        log.info("Balance updated for accountId: {}", id);
    }

    public AccountResponse getMyAccountByNumber(String accountNumber) {
        Long customerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Customer {} fetching account: {}", customerId, accountNumber);
        Account acc = repo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        if (!acc.getCustomerId().equals(customerId)) {
            log.warn("Unauthorised access - customerId: {} tried to access account: {}", customerId, accountNumber);
            throw new UnauthorisedAccessException("Unauthorised access to account");
        }
        return mapToResponse(acc);
    }

    public void closeAccount(Long id) {
        Long customerId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account acc = repo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
        if (!acc.getCustomerId().equals(customerId)) {
            throw new UnauthorisedAccessException("Unauthorised access to account");
        }
        acc.setStatus(AccountStatus.CLOSED);
        repo.save(acc);
        log.info("Account {} closed by customerId: {}", id, customerId);
        eventPublisher.publish(new AccountEvent(acc.getId(), acc.getAccountNumber(), acc.getCustomerId(), "ACCOUNT_CLOSED", LocalDateTime.now()));
    }

    public void freezeAccount(Long id) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
        acc.setStatus(AccountStatus.FROZEN);
        repo.save(acc);
        log.info("Account {} frozen by admin", id);
        eventPublisher.publish(new AccountEvent(acc.getId(), acc.getAccountNumber(), acc.getCustomerId(), "ACCOUNT_FROZEN", LocalDateTime.now()));
    }

    private AccountResponse mapToResponse(Account acc) {
        return new AccountResponse(acc.getId(), acc.getAccountNumber(),
                acc.getAccountType(), acc.getBalance(), acc.getStatus());
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
