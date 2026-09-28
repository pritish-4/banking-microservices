package com.example.banking.service;

import com.example.banking.dto.AccountDTO;
import com.example.banking.dto.AccountResponseDTO;
import com.example.banking.entity.Account;
import com.example.banking.exceptions.ResourceNotFoundException;
import com.example.banking.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepo accountRepo;

    public AccountResponseDTO createAccount(AccountDTO dto){
        Account account = mapToEntity(dto);
        accountRepo.save(account);
        return mapToDto(account);
    }

    public AccountResponseDTO getAccount(int id){
        Optional<Account> account = accountRepo.findById(id);
        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with id: " + id + " doesn't exist");
        }
        return mapToDto(account.get());
    }

    private AccountResponseDTO mapToDto(Account account) {
        return new AccountResponseDTO(account.getId(), account.getName(), account.getBalance());
    }



    private Account mapToEntity(AccountDTO dto){
        Account account = new Account();
        account.setName(dto.name());
        account.setBalance(dto.balance());
        return account;
    }

}
