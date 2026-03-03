package com.springboot.transaction.repositories;

import com.springboot.transaction.entities.TransactionRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRecordRepository extends CrudRepository<TransactionRecord, String> {

    List<TransactionRecord> findByFromAccountNumberOrToAccountNumberOrderByCreatedAtDesc(
            String fromAccountNumber,
            String toAccountNumber
    );
}
