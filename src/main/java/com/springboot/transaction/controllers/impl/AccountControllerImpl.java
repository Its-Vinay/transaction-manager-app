package com.springboot.transaction.controllers.impl;

import com.springboot.tm.spec.dto.AccountRequestDTO;
import com.springboot.tm.spec.dto.AccountResponseDTO;
import com.springboot.tm.spec.services.AccountService;
import com.springboot.transaction.controllers.AccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountControllerImpl implements AccountController {

    @Autowired
    private AccountService accountService;

    @Override
    public ResponseEntity<AccountResponseDTO> createAccount(AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO accountResponseDTO = accountService.createAccount(accountRequestDTO);
        return new ResponseEntity<>(accountResponseDTO, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AccountResponseDTO> inquireAccount(String accountNumber) {
        AccountResponseDTO accountResponseDTO = accountService.inquireAccount(accountNumber);
        return new ResponseEntity<>(accountResponseDTO, HttpStatus.OK);
    }
}
