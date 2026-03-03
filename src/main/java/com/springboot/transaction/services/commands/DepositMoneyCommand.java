package com.springboot.transaction.services.commands;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.transaction.entities.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositMoneyCommand implements MoneyOperationCommand {

    @Override
    public CrDrIndicator type() {
        return CrDrIndicator.CREDIT;
    }

    @Override
    public void apply(Account account, BigDecimal amount) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        account.setBalance(account.getBalance().add(amount));
    }
}
