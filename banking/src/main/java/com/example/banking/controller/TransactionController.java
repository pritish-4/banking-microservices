package com.example.banking.controller;

import com.example.banking.dto.TransactionDTO;
import com.example.banking.dto.TransactionResponseDTO;
import com.example.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransactionDTO dto){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.transfer(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransaction(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable int accountId){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionsForAccount(accountId));
    }
}
