package com.springboot.transaction.services.utils;

import com.springboot.tm.spec.dto.AccountRequestDTO;
import com.springboot.tm.spec.dto.AccountResponseDTO;
import com.springboot.tm.spec.dto.FreezeStatus;
import com.springboot.tm.spec.dto.Status;
import com.springboot.tm.spec.dto.TransactionInquiryDTO;
import com.springboot.transaction.domain.BankCode;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.entities.TransactionRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountServiceUtil {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public Account createAccountFromDto(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO == null) {
            throw new IllegalArgumentException("accountRequestDTO must not be null");
        }

        Account account = new Account();
        account.setAccountNumber(requireText(accountRequestDTO.getAccountNumber(), "accountNumber"));
        account.setAccountName(requireText(accountRequestDTO.getAccountName(), "accountName"));
        account.setStatus(parseStatus(accountRequestDTO.getStatus()));
        account.setFreezeStatus(parseFreezeStatus(accountRequestDTO.getFreezeStatus()));
        account.setBankId(BankCode.resolveFromAccountNumber(accountRequestDTO.getAccountNumber()));
        account.setBalance(ZERO);
        return account;
    }

    public AccountResponseDTO populateAccountResponseDto(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setAccountNumber(account.getAccountNumber());
        accountResponseDTO.setAccountName(account.getAccountName());
        accountResponseDTO.setStatus(account.getStatus());
        accountResponseDTO.setFreezeStatus(account.getFreezeStatus());
        return accountResponseDTO;
    }

    public TransactionInquiryDTO populateTransactionInquiryDto(TransactionRecord transactionRecord) {
        TransactionInquiryDTO inquiryDTO = new TransactionInquiryDTO();
        inquiryDTO.setTransactionType(transactionRecord.getTransactionType());
        inquiryDTO.setTransactionStatus(transactionRecord.getTransactionStatus());
        inquiryDTO.setAmount(transactionRecord.getAmount());
        inquiryDTO.setFromAccountNumber(transactionRecord.getFromAccountNumber());
        inquiryDTO.setToAccountNumber(transactionRecord.getToAccountNumber());
        inquiryDTO.setBankId(transactionRecord.getBankId());
        inquiryDTO.setFailureReason(transactionRecord.getFailureReason());
        inquiryDTO.setCreatedAt(transactionRecord.getCreatedAt());
        return inquiryDTO;
    }

    public String parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status must not be blank");
        }

        try {
            return Status.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Allowed values: "
                    + allowedStatusValues(), ex);
        }
    }

    public String parseFreezeStatus(String freezeStatus) {
        if (freezeStatus == null || freezeStatus.isBlank()) {
            throw new IllegalArgumentException("freezeStatus must not be blank");
        }

        try {
            return FreezeStatus.valueOf(freezeStatus.trim().toUpperCase(Locale.ROOT)).name();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid freezeStatus: " + freezeStatus + ". Allowed values: "
                    + allowedFreezeStatusValues(), ex);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private String newId(String candidateId) {
        if (candidateId == null || candidateId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return candidateId.trim();
    }

    private String allowedStatusValues() {
        return Arrays.stream(Status.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private String allowedFreezeStatusValues() {
        return Arrays.stream(FreezeStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
