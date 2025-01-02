package com.bank_statement_reader.dkb.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank_statement_reader.dkb.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findByOriginalValue(String originalValue);

    @Query("SELECT t FROM Transaction t WHERE t.bookingDate >= :startDate")
    List<Transaction> findTransactionsFrom(@Param("startDate") LocalDate startDate);

    List<Transaction> findByYear(int year);
}
