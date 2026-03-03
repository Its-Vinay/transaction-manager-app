package com.springboot.transaction.services.commands;

import com.springboot.tm.spec.dto.CrDrIndicator;
import com.springboot.transaction.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MoneyOperationProcessor {

    @Autowired
    private List<MoneyOperationCommand> commands;

    private Map<CrDrIndicator, MoneyOperationCommand> commandsByType;

    @PostConstruct
    public void init() {
        this.commandsByType = new EnumMap<>(CrDrIndicator.class);
        for (MoneyOperationCommand command : commands) {
            this.commandsByType.put(command.type(), command);
        }
    }

    public Account process(CrDrIndicator crDrIndicator, Account account, BigDecimal amount) {
        MoneyOperationCommand command = commandsByType.get(crDrIndicator);
        if (command == null) {
            throw new IllegalArgumentException("No command registered for: " + crDrIndicator);
        }

        command.apply(account, amount);
        return account;
    }
}
