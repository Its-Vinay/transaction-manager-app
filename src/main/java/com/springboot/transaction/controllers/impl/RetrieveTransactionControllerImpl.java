package com.springboot.transaction.controllers.impl;

import com.springboot.tm.spec.dto.TransactionInquiryDTO;
import com.springboot.tm.spec.services.TranInquiryService;
import com.springboot.transaction.controllers.RetrieveTransactionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RetrieveTransactionControllerImpl implements RetrieveTransactionController {

    @Autowired
    private TranInquiryService tranInquiryService;

    @Override
    public ResponseEntity<List<TransactionInquiryDTO>> history(String accountNumber) {
        List<TransactionInquiryDTO> inquiryDTO = tranInquiryService.retrieveTran(accountNumber);
        return new ResponseEntity<>(inquiryDTO, HttpStatus.OK);
    }
}
