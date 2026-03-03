package com.springboot.transaction.services.impl;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.services.CreditService;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import com.springboot.transaction.services.commands.MoneyOperationProcessor;
import com.springboot.transaction.services.utils.CreditServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private MoneyOperationProcessor moneyOperationProcessor;

    @Autowired
    private CreditServiceUtil creditServiceUtil;

    @Override
    @Transactional
    public TransactionResponseDTO credit(TransactionRequestDTO transactionRequestDTO) {
        creditServiceUtil.validateCreditRequest(transactionRequestDTO);

        Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + transactionRequestDTO.getAccountNumber()));

        BigDecimal amount = creditServiceUtil.validateAmount(transactionRequestDTO.getAmount());
        creditServiceUtil.validateCreditTransactionAllowed(account);
        moneyOperationProcessor.process(CrDrIndicator.CREDIT, account, amount);
        accountRepository.save(account);

        TransactionRecord record = creditServiceUtil.createCreditTransactionRecord(account, amount);
        transactionRecordRepository.save(record);

        return creditServiceUtil.populateTransactionResponseDto(record);
    }
}
