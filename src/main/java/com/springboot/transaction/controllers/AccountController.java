package com.springboot.transaction.controllers;

import com.springboot.tm.spec.dto.AccountRequestDTO;
import com.springboot.tm.spec.dto.AccountResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/transactionManager")
public interface AccountController {

    @PostMapping(value = "/account/create", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO accountRequestDTO);

    @GetMapping(value = "/account/inquire/{accountNumber}")
    ResponseEntity<AccountResponseDTO> inquireAccount(@PathVariable("accountNumber") String accountNumber);
}
