package com.springboot.transaction.domain;

import java.util.Arrays;
import java.util.Locale;

public enum BankCode {
    HDFC,
    ICICI,
    SBI;

    public static String resolveFromAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank() || !accountNumber.contains("-")) {
            throw new IllegalArgumentException("Account number must follow <BANK>-<NUMBER> format, e.g. HDFC-1001");
        }

        String prefix = accountNumber.substring(0, accountNumber.indexOf('-')).trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .map(Enum::name)
                .filter(prefix::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported bank code: " + prefix
                        + ". Allowed values: HDFC, ICICI, SBI"));
    }
}
