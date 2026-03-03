package com.springboot.transaction.services.impl;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.services.DebitService;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import com.springboot.transaction.services.commands.MoneyOperationProcessor;
import com.springboot.transaction.services.utils.DebitServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class DebitServiceImpl implements DebitService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private MoneyOperationProcessor moneyOperationProcessor;

    @Autowired
    private DebitServiceUtil debitServiceUtil;

    @Override
    @Transactional
    public TransactionResponseDTO debit(TransactionRequestDTO transactionRequestDTO) {
        log.debug("credit transaction with request dto is : {}", transactionRequestDTO);
        debitServiceUtil.validateDebitRequest(transactionRequestDTO);

        Account account = accountRepository.findByAccountNumber(transactionRequestDTO.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + transactionRequestDTO.getAccountNumber()));

        BigDecimal amount = debitServiceUtil.validateAmount(transactionRequestDTO.getAmount());
        debitServiceUtil.validateDebitTransactionAllowed(account, amount);
        moneyOperationProcessor.process(CrDrIndicator.DEBIT, account, amount);
        log.debug("updating balance to : {}", account.getBalance());
        accountRepository.save(account);

        TransactionRecord record = debitServiceUtil.createDebitTransactionRecord(account, amount);
        log.debug("transaction record saved is  : {}", record);
        transactionRecordRepository.save(record);

        return debitServiceUtil.populateTransactionResponseDto(record);
    }
}
