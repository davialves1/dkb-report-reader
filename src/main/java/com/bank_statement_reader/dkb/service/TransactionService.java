package com.bank_statement_reader.dkb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.mapper.TransactionMapper;
import com.bank_statement_reader.dkb.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionDto convertToDto(Transaction transaction) {
        return TransactionMapper.mapper.convertToDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(t -> convertToDto(t)).toList();
    }

    public List<TransactionDto> getLast30Days() {
        LocalDate last30Days = LocalDate.now().minusDays(30);
        return transactionRepository.findTransactionsFromLast30Days(last30Days).stream().map(t -> convertToDto(t))
                .toList();
    }
}
