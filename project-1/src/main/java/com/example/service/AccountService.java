package com.example.service;

import java.util.Objects;

import javax.persistence.EntityExistsException;

import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public Account createAccount(Account account) {

        // Check username not already taken
        // Return 409 - conflict
        boolean usernameTaken = usernameTaken(account.getUsername());
        if(usernameTaken)
            throw new EntityExistsException("Account already exists");

        Account savedAccount = repo.save(account);
        return savedAccount;        
    }

    public Account getAccountById(int accountId) {
        return findAccountById(accountId);
    }

    public Account getAccountByUsername(String userName) {
        return findAccountByUsername(userName);
    }


    // PRIVATE --------------------------------------------//
    private Account findAccountById(int accountId) {
        return repo.findById(accountId).orElseThrow();
    }

    private Account findAccountByUsername(String username) {
        return repo.findByUsername(username);
    }

    private boolean usernameTaken(String username) {
        Account account = findAccountByUsername(username);

        if(Objects.isNull(account))
            return false;

        return true;
    }

}
