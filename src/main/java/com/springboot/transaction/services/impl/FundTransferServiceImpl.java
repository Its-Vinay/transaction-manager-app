package com.springboot.transaction.services.impl;

import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.dto.TransferRequestDTO;
import com.springboot.tm.spec.services.FundTransferService;
import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import com.springboot.transaction.repositories.AccountRepository;
import com.springboot.transaction.repositories.TransactionRecordRepository;
import com.springboot.transaction.services.commands.MoneyOperationProcessor;
import com.springboot.transaction.services.utils.TransferServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;

@Service
@Slf4j
public class FundTransferServiceImpl implements FundTransferService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private MoneyOperationProcessor moneyOperationProcessor;

    @Autowired
    private TransferServiceUtil transferServiceUtil;

    @Autowired
    private TransferTransactionLogService transferTransactionLogService;

    @Override
    @Transactional
    public TransactionResponseDTO fundTransfer(TransferRequestDTO transferRequestDTO) {
        log.debug("credit transaction with request dto is : {}", transferRequestDTO);
        transferServiceUtil.validateTransferRequest(transferRequestDTO);

        Account source = accountRepository.findByAccountNumber(transferRequestDTO.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Source account not found: " + transferRequestDTO.getSourceAccountNumber()));
        Account destination = accountRepository.findByAccountNumber(transferRequestDTO.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Destination account not found: " + transferRequestDTO.getDestinationAccountNumber()));

        transferServiceUtil.validateAccountsForTransfer(source, destination);

        BigDecimal amount = transferServiceUtil.validateAmount(transferRequestDTO.getAmount());
        try {
            transferServiceUtil.validateWithdrawalAllowed(source, amount);
            moneyOperationProcessor.process(CrDrIndicator.DEBIT, source, amount);
            log.debug("updating balance of source account to : {}", source.getBalance());
            accountRepository.save(source);

            transferServiceUtil.validateDepositAllowed(destination);
            moneyOperationProcessor.process(CrDrIndicator.CREDIT, destination, amount);
            log.debug("updating balance of destination account to : {}", destination.getBalance());
            accountRepository.save(destination);

            TransactionRecord completed = transferServiceUtil.createCompletedTransferRecord(source, destination, amount);
            log.debug("transaction record saved is  : {}", completed);
            transactionRecordRepository.save(completed);
            return transferServiceUtil.populateTransactionResponseDto(completed);
        } catch (RuntimeException ex) {
            log.error("failed fund transfer API with error : {}", ex.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            TransactionRecord failed = transferTransactionLogService.saveFailedTransfer(source, destination, amount, ex.getMessage());
            log.debug("transaction record saved is  : {}", failed);
            return transferServiceUtil.populateTransactionResponseDto(failed);
        }
    }
}
