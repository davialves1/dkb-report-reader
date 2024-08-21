package com.bank_statement_reader.dkb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.service.TransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class UpdateController {

    private final TransactionService transactionService;

    @PostMapping(value = "/api/update")
    public ResponseEntity<Transaction> update(@RequestBody TransactionDto entity) {
        Transaction transaction = transactionService.updateTransaction(entity);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

}
