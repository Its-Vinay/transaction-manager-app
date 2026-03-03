package com.springboot.transaction.controllers;

import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/transactionManager")
public interface DebitController {

    @PostMapping(value = "/debit", consumes = "application/json")
    ResponseEntity<TransactionResponseDTO> withdraw(@RequestBody TransactionRequestDTO transactionRequestDTO);
}
