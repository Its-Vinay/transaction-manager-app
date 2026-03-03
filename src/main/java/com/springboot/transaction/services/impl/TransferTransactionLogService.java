package com.springboot.transaction.services.impl;

import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import com.springboot.transaction.services.utils.TransferServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferTransactionLogService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private TransferServiceUtil transferServiceUtil;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionRecord saveFailedTransfer(
            Account source,
            Account destination,
            BigDecimal amount,
            String failureReason
    ) {
        TransactionRecord failed = transferServiceUtil.createFailedTransferRecord(
                source,
                destination,
                amount,
                failureReason
        );
        return transactionRecordRepository.save(failed);
    }
}
