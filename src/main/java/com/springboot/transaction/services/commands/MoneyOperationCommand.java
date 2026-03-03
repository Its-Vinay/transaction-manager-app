package com.springboot.transaction.services.commands;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.transaction.entities.Account;

import java.math.BigDecimal;

public interface MoneyOperationCommand {

    CrDrIndicator type();

    void apply(Account account, BigDecimal amount);
}
