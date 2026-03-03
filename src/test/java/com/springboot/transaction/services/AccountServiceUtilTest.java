package com.springboot.transaction.services;

import com.springboot.tm.spec.dto.AccountRequestDTO;
import com.springboot.transaction.entities.Account;
import com.springboot.transaction.services.utils.AccountServiceUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceUtilTest {

    private final AccountServiceUtil accountServiceUtil = new AccountServiceUtil();

    @Test
    void createAccountFromDtoShouldPopulateEntityAndResolveBank() {
        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setAccountNumber("HDFC-1001");
        requestDTO.setAccountName("John");
        requestDTO.setStatus("active");
        requestDTO.setFreezeStatus("not_frozen");

        Account account = accountServiceUtil.createAccountFromDto(requestDTO);

        assertNotNull(account.getAccountId());
        assertEquals("HDFC-1001", account.getAccountNumber());
        assertEquals("HDFC", account.getBankId());
        assertEquals("ACTIVE", account.getStatus());
        assertEquals("NOT_FROZEN", account.getFreezeStatus());
        assertEquals(0, account.getBalance().signum());
    }

    @Test
    void createAccountFromDtoShouldThrowForInvalidStatus() {
        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setAccountNumber("ICICI-2001");
        requestDTO.setAccountName("Alice");
        requestDTO.setStatus("open");
        requestDTO.setFreezeStatus("NOT_FROZEN");

        assertThrows(IllegalArgumentException.class, () -> accountServiceUtil.createAccountFromDto(requestDTO));
    }
}
