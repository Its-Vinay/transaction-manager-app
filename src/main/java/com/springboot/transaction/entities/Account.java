package com.springboot.transaction.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue
    private String accountId;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String freezeStatus;

    @Column(nullable = false)
    private String bankId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
}
