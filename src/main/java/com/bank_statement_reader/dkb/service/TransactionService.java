package com.bank_statement_reader.dkb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.mapper.TransactionMapper;
import com.bank_statement_reader.dkb.repository.TransactionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionDto convertToDto(Transaction transaction) {
        return TransactionMapper.mapper.convertToDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(t -> convertToDto(t)).toList();
    }
}
