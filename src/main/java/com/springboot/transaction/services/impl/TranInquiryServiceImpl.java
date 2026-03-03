package com.springboot.transaction.services.impl;

import com.springboot.tm.spec.dto.TransactionInquiryDTO;
import com.springboot.tm.spec.services.TranInquiryService;
import com.springboot.transaction.entities.TransactionRecord;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import com.springboot.transaction.services.utils.AccountServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranInquiryServiceImpl implements TranInquiryService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private AccountServiceUtil accountServiceUtil;

    @Override
    public TransactionInquiryDTO retrieveTran(String accountNumber) {
        accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));

        List<TransactionRecord> records = transactionRecordRepository
                .findByFromAccountNumberOrToAccountNumberOrderByCreatedAtDesc(accountNumber, accountNumber);

        if (records.isEmpty()) {
            throw new IllegalArgumentException("No transactions found for account: " + accountNumber);
        }

        return accountServiceUtil.populateTransactionInquiryDto(records.get(0));
    }
}
