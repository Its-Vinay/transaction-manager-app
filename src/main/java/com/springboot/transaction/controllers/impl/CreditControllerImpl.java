package com.springboot.transaction.controllers.impl;

import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.services.CreditService;
import com.springboot.transaction.controllers.CreditController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditControllerImpl implements CreditController {

    @Autowired
    private CreditService creditService;

    @Override
    public ResponseEntity<TransactionResponseDTO> deposit(TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO responseDTO = creditService.credit(transactionRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
