package com.bank_statement_reader.dkb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank_statement_reader.dkb.entity.Balance;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Balance findFirstByOrderByUpdateDesc();
}
