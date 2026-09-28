package com.example.banking.service;

import com.example.banking.dto.TransactionDTO;
import com.example.banking.dto.TransactionResponseDTO;
import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.exceptions.InsufficientBalanceException;
import com.example.banking.exceptions.ResourceNotFoundException;
import com.example.banking.repo.AccountRepo;
import com.example.banking.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;

    @Transactional
    public TransactionResponseDTO transfer(TransactionDTO dto) {
        Optional<Account> optionalFromAccount = accountRepo.findById(dto.fromId());
        if(optionalFromAccount.isEmpty()){
            throw new ResourceNotFoundException("Account with id: " + dto.fromId() + " doesn't exist");
        }
        Optional<Account> optionalToAccount = accountRepo.findById(dto.toId());
        if(optionalToAccount.isEmpty()){
            throw new ResourceNotFoundException("Account with id: " + dto.toId() + " doesn't exist");
        }
        Account fromAccount = optionalFromAccount.get();
        if(fromAccount.getBalance() < dto.amount()){
            throw new InsufficientBalanceException("Not enough balance");
        }
        Account toAccount = optionalToAccount.get();
        fromAccount.setBalance(fromAccount.getBalance() - dto.amount());
        toAccount.setBalance(toAccount.getBalance() + dto.amount());
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);
        Transaction transaction = mapToEntity(dto);
        transactionRepo.save(transaction);
        return mapToDto(transaction);
    }

    public TransactionResponseDTO getTransaction(int id) {
        Optional<Transaction> optTransaction = transactionRepo.findById(id);
        if(optTransaction.isEmpty()){
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");
        }
        Transaction transaction = optTransaction.get();
        return mapToDto(transaction);
    }


    @Transactional
    public List<TransactionResponseDTO> getTransactionsForAccount(int accountId) {
        if (!accountRepo.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found");
        }
        return transactionRepo.findAllByAccountId(accountId)
                .stream()
                .map(e -> mapToDto(e))
                .toList();
    }


    private Transaction mapToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFrom(accountRepo.getReferenceById(dto.fromId()));
        transaction.setTo(accountRepo.getReferenceById(dto.toId()));
        transaction.setAmount(dto.amount());
        transaction.setTime(LocalDateTime.now());
        return transaction;
    }

    private TransactionResponseDTO mapToDto(Transaction transaction){
        return new TransactionResponseDTO(transaction.getId(), transaction.getFrom().getId(), transaction.getTo().getId(), transaction.getAmount(), LocalDateTime.now());
    }
}

