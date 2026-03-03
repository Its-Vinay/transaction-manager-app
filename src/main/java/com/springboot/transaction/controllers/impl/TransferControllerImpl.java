package com.springboot.transaction.controllers.impl;

import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.dto.TransferRequestDTO;
import com.springboot.tm.spec.services.FundTransferService;
import com.springboot.transaction.controllers.TransferController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferControllerImpl implements TransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @Override
    public ResponseEntity<TransactionResponseDTO> transfer(TransferRequestDTO transferRequestDTO) {
        TransactionResponseDTO responseDTO = fundTransferService.fundTransfer(transferRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
