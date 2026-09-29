package com.banking.transaction.service;

import com.banking.transaction.client.AccountClient;
import com.banking.transaction.dto.AccountBalanceResponse;
import com.banking.transaction.dto.CustomerAccountResponse;
import com.banking.transaction.dto.DepositRequest;
import com.banking.transaction.dto.TransactionResponse;
import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.dto.WithdrawRequest;
import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionType;
import com.banking.transaction.event.TransactionEvent;
import com.banking.transaction.event.TransactionEventPublisher;
import com.banking.transaction.exception.InsufficientFundsException;
import com.banking.transaction.exception.TransactionNotFoundException;
import com.banking.transaction.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepo repo;
    private final AccountClient accountClient;
    private final TransactionEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse deposit(DepositRequest request, String authHeader) {
        log.info("Deposit request - accountId: {}, amount: {}", request.accountId(), request.amount());
        AccountBalanceResponse account = accountClient.getAccount(request.accountId(), authHeader);

        accountClient.updateBalance(request.accountId(), account.balance().add(request.amount()), authHeader);

        Transaction tx = buildTransaction(null, request.accountId(), request.amount(), TransactionType.DEPOSIT);
        repo.save(tx);
        eventPublisher.publish(toEvent(tx));

        log.info("Deposit successful - txId: {}, accountId: {}, amount: {}", tx.getTransactionId(), request.accountId(), request.amount());
        return mapToResponse(tx);
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request, String authHeader) {
        log.info("Withdraw request - accountId: {}, amount: {}", request.accountId(), request.amount());
        AccountBalanceResponse account = accountClient.getAccount(request.accountId(), authHeader);

        if (account.balance().compareTo(request.amount()) < 0) {
            log.warn("Insufficient funds - accountId: {}, balance: {}, requested: {}",
                    request.accountId(), account.balance(), request.amount());
            throw new InsufficientFundsException("Insufficient funds in account: " + request.accountId());
        }

        accountClient.updateBalance(request.accountId(), account.balance().subtract(request.amount()), authHeader);

        Transaction tx = buildTransaction(request.accountId(), null, request.amount(), TransactionType.WITHDRAW);
        repo.save(tx);
        eventPublisher.publish(toEvent(tx));

        log.info("Withdraw successful - txId: {}, accountId: {}, amount: {}", tx.getTransactionId(), request.accountId(), request.amount());
        return mapToResponse(tx);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String authHeader) {
        log.info("Transfer request - from: {}, to: {}, amount: {}",
                request.fromAccountId(), request.toAccountId(), request.amount());

        Long firstLock = Math.min(request.fromAccountId(), request.toAccountId());
        Long secondLock = Math.max(request.fromAccountId(), request.toAccountId());
        repo.findByIdWithLock(firstLock);
        repo.findByIdWithLock(secondLock);

        AccountBalanceResponse source = accountClient.getAccount(request.fromAccountId(), authHeader);
        AccountBalanceResponse target = accountClient.getAccount(request.toAccountId(), authHeader);

        if (source.balance().compareTo(request.amount()) < 0) {
            log.warn("Insufficient funds for transfer - fromAccountId: {}, balance: {}, requested: {}",
                    request.fromAccountId(), source.balance(), request.amount());
            throw new InsufficientFundsException("Insufficient funds in account: " + request.fromAccountId());
        }

        accountClient.updateBalance(request.fromAccountId(), source.balance().subtract(request.amount()), authHeader);
        accountClient.updateBalance(request.toAccountId(), target.balance().add(request.amount()), authHeader);

        Transaction tx = buildTransaction(request.fromAccountId(), request.toAccountId(), request.amount(), TransactionType.TRANSFER);
        repo.save(tx);
        eventPublisher.publish(toEvent(tx));

        log.info("Transfer successful - txId: {}, from: {}, to: {}, amount: {}",
                tx.getTransactionId(), request.fromAccountId(), request.toAccountId(), request.amount());
        return mapToResponse(tx);
    }

    public List<TransactionResponse> getMyTransactions(String authHeader) {
        List<Long> accountIds = accountClient.getMyAccounts(authHeader)
                .stream().map(CustomerAccountResponse::accountId).toList();
        log.info("Fetching transactions for accountIds: {}", accountIds);
        return accountIds.stream()
                .flatMap(id -> repo.findBySourceAccountIdOrTargetAccountId(id, id).stream())
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TransactionResponse> getMyTransactionsByType(TransactionType type, String authHeader) {
        List<Long> accountIds = accountClient.getMyAccounts(authHeader)
                .stream().map(CustomerAccountResponse::accountId).toList();
        log.info("Fetching {} transactions for accountIds: {}", type, accountIds);
        return accountIds.stream()
                .flatMap(id -> repo.findBySourceAccountIdOrTargetAccountIdAndType(id, id, type).stream())
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }

    public TransactionResponse getByTransactionId(String transactionId) {
        log.info("Fetching transaction by ref: {}", transactionId);
        return repo.findByTransactionId(transactionId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + transactionId));
    }

    public List<TransactionResponse> getAllTransactions() {
        log.info("Admin fetching all transactions");
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        log.info("Admin fetching transactions for accountId: {}", accountId);
        return repo.findBySourceAccountIdOrTargetAccountId(accountId, accountId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getTransactionId(),
                tx.getType(),
                tx.getSourceAccountId(),
                tx.getTargetAccountId(),
                tx.getAmount(),
                tx.getStatus(),
                tx.getCreatedAt()
        );
    }

    private TransactionEvent toEvent(Transaction tx) {
        return new TransactionEvent(
                tx.getTransactionId(), tx.getType(),
                tx.getSourceAccountId(), tx.getTargetAccountId(),
                tx.getAmount(), tx.getStatus(), tx.getCreatedAt()
        );
    }

    private Transaction buildTransaction(Long sourceId, Long targetId, BigDecimal amount, TransactionType type) {
        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setSourceAccountId(sourceId);
        tx.setTargetAccountId(targetId);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setStatus("SUCCESS");
        tx.setCreatedAt(LocalDateTime.now());
        return tx;
    }
}
