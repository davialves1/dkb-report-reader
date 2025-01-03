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

    public Transaction updateTransaction(TransactionDto transactionDto) {
        Transaction transaction = this.convertToEntityTransaction(transactionDto);
        return transactionRepository.save(transaction);
    }

    public Transaction convertToEntityTransaction(TransactionDto transactionDto) {
        return TransactionMapper.mapper.convertToToEntity(transactionDto);
    }

    public TransactionDto convertToDto(Transaction transaction) {
        return TransactionMapper.mapper.convertToDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(t -> convertToDto(t)).toList();
    }

    public List<TransactionDto> findByYear(int year) {
        return transactionRepository.findByYear(year).stream().map(t -> convertToDto(t)).toList();

    }

    public List<TransactionDto> findAllFromCurrentYear() {
        int currentYear = java.time.Year.now().getValue();
        return transactionRepository.findByYear(currentYear).stream().map(t -> convertToDto(t)).toList();
    }

    public List<TransactionDto> getLast30Days() {
        LocalDate last30Days = LocalDate.now().minusDays(30);
        return transactionRepository
                .findTransactionsFrom(last30Days)
                .stream()
                .map(t -> convertToDto(t))
                .toList();
    }

    public List<TransactionDto> getPrevious(int days) {
        LocalDate previousDays = LocalDate.now().minusDays(days);
        return transactionRepository
                .findTransactionsFrom(
                        previousDays)
                .stream()
                .map(t -> convertToDto(t))
                .toList();
    }
}
