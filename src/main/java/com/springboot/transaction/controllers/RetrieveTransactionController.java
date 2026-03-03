package com.springboot.transaction.controllers;

import com.springboot.tm.spec.dto.TransactionInquiryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/transactionManager")
public interface RetrieveTransactionController {

    @GetMapping(value = "/retrieveTransaction/account/{accountNumber}")
    ResponseEntity<List<TransactionInquiryDTO>> history(@PathVariable("accountNumber") String accountNumber);
}
