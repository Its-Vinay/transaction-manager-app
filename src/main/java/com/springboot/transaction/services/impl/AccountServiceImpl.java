package com.springboot.transaction.services.impl;

import com.springboot.tm.spec.dto.AccountRequestDTO;
import com.springboot.tm.spec.dto.AccountResponseDTO;
import com.springboot.tm.spec.services.AccountService;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.services.utils.AccountServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountServiceUtil accountServiceUtil;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        Account account = accountServiceUtil.createAccountFromDto(accountRequestDTO);
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists: " + account.getAccountNumber());
        }

        accountRepository.save(account);
        return accountServiceUtil.populateAccountResponseDto(account);
    }

    @Override
    public AccountResponseDTO inquireAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        return accountServiceUtil.populateAccountResponseDto(account);
    }
}
