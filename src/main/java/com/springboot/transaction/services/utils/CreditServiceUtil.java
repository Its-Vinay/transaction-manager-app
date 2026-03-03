package com.springboot.transaction.services.utils;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.tm.spec.dto.FreezeStatus;
import com.springboot.tm.spec.dto.TransactionRequestDTO;
import com.springboot.tm.spec.dto.TransactionResponseDTO;
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
public class CreditServiceUtil {

    public void validateCreditRequest(TransactionRequestDTO transactionRequestDTO) {
        if (transactionRequestDTO == null) {
            throw new IllegalArgumentException("transactionRequestDTO must not be null");
        }

        String indicator = requireText(transactionRequestDTO.getCrDrIndicator(), "CrDrIndicator").toUpperCase(Locale.ROOT);
        if (!CrDrIndicator.CREDIT.name().equals(indicator)) {
            throw new IllegalArgumentException("CrDrIndicator must be CREDIT");
        }
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

    public void validateCreditTransactionAllowed(Account account) {
        String freezeStatus = requireText(account.getFreezeStatus(), "freezeStatus").toUpperCase(Locale.ROOT);
        if (FreezeStatus.TOTAL_FREEZE.name().equals(freezeStatus)
                || FreezeStatus.CREDIT_FREEZE.name().equals(freezeStatus)) {
            throw new IllegalArgumentException("Deposit is blocked for freeze status: " + freezeStatus);
        }
    }

    public TransactionRecord createCreditTransactionRecord(Account account, BigDecimal amount) {
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(UUID.randomUUID().toString());
        record.setTransactionType(TransactionType.DEPOSIT.name());
        record.setTransactionStatus(TransactionStatus.SUCCESS.name());
        record.setAmount(amount);
        record.setFromAccountNumber(account.getAccountNumber());
        record.setToAccountNumber(account.getAccountNumber());
        record.setBankId(account.getBankId());
        record.setCreatedAt(LocalDateTime.now());
        record.setFailureReason(null);
        return record;
    }

    public TransactionResponseDTO populateTransactionResponseDto(TransactionRecord transactionRecord) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransactionId(transactionRecord.getTransactionId());
        responseDTO.setStatus(transactionRecord.getTransactionStatus());
        return responseDTO;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
