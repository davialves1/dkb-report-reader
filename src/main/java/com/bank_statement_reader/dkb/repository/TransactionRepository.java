package com.bank_statement_reader.dkb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank_statement_reader.dkb.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findByOriginalValue(String originalValue);

}
