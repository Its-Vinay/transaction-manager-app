package com.springboot.transaction.controllers;

import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.dto.TransferRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/transactionManager")
public interface TransferController {

    @PostMapping(value = "/fundTransfer", consumes = "application/json")
    ResponseEntity<TransactionResponseDTO> transfer(@RequestBody TransferRequestDTO transferRequestDTO);
}
