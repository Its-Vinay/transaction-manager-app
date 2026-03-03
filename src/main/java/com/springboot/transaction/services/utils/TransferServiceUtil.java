package com.springboot.transaction.services.utils;

import com.springboot.tm.spec.dto.FreezeStatus;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
import com.springboot.tm.spec.dto.TransferRequestDTO;
import com.springboot.transaction.domain.TransactionStatus;
import com.springboot.transaction.domain.TransactionType;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Component
public class TransferServiceUtil {

    public void validateTransferRequest(TransferRequestDTO transferRequestDTO) {
        if (transferRequestDTO == null) {
            throw new IllegalArgumentException("transferRequestDTO must not be null");
        }

        requireText(transferRequestDTO.getSourceAccountNumber(), "sourceAccountNumber");
        requireText(transferRequestDTO.getDestinationAccountNumber(), "destinationAccountNumber");
    }

    public BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public void validateAccountsForTransfer(Account source, Account destination) {
        if (source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new IllegalArgumentException("Source and destination account must be different");
        }
    }

    public void validateWithdrawalAllowed(Account account, BigDecimal amount) {
        String freezeStatus = requireText(account.getFreezeStatus(), "freezeStatus").toUpperCase(Locale.ROOT);
        if (FreezeStatus.TOTAL_FREEZE.name().equals(freezeStatus)
                || FreezeStatus.DEBIT_FREEZE.name().equals(freezeStatus)) {
            throw new IllegalArgumentException("Withdrawal is blocked for freeze status: " + freezeStatus);
        }

        if (account.getBalance() == null || account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    public void validateDepositAllowed(Account account) {
        String freezeStatus = requireText(account.getFreezeStatus(), "freezeStatus").toUpperCase(Locale.ROOT);
        if (FreezeStatus.TOTAL_FREEZE.name().equals(freezeStatus)
                || FreezeStatus.CREDIT_FREEZE.name().equals(freezeStatus)) {
            throw new IllegalArgumentException("Deposit is blocked for freeze status: " + freezeStatus);
        }
    }

    public TransactionRecord createCompletedTransferRecord(Account source, Account destination, BigDecimal amount) {
        return createTransferRecord(TransactionStatus.SUCCESS, source, destination, amount, null);
    }

    public TransactionRecord createFailedTransferRecord(
            Account source,
            Account destination,
            BigDecimal amount,
            String failureReason
    ) {
        return createTransferRecord(TransactionStatus.FAILED, source, destination, amount, failureReason);
    }

    public TransactionResponseDTO populateTransactionResponseDto(TransactionRecord transactionRecord) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransactionId(transactionRecord.getTransactionId());
        responseDTO.setStatus(transactionRecord.getTransactionStatus());
        return responseDTO;
    }

    private TransactionRecord createTransferRecord(
            TransactionStatus status,
            Account source,
            Account destination,
            BigDecimal amount,
            String failureReason
    ) {
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(UUID.randomUUID().toString());
        record.setTransactionType(TransactionType.TRANSFER.name());
        record.setTransactionStatus(status.name());
        record.setAmount(amount);
        record.setFromAccountNumber(source.getAccountNumber());
        record.setToAccountNumber(destination.getAccountNumber());
        record.setBankId(source.getBankId());
        record.setCreatedAt(LocalDateTime.now());
        record.setFailureReason(failureReason);
        return record;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
