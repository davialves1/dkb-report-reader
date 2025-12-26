package com.bank_statement_reader.dkb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank_statement_reader.dkb.entity.Investment;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    
}
