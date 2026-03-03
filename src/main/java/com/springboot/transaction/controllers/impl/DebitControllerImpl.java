package com.springboot.transaction.controllers.impl;

import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.services.DebitService;
import com.springboot.transaction.controllers.DebitController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebitControllerImpl implements DebitController {

    @Autowired
    private DebitService debitService;

    @Override
    public ResponseEntity<TransactionResponseDTO> withdraw(TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO responseDTO = debitService.debit(transactionRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
