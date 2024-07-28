package com.bank_statement_reader.dkb.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;

    @GetMapping("/dashboard")
    public ResponseEntity<List<TransactionDto>> getDashboard() {
        List<TransactionDto> last30Days = transactionService.getLast30Days();
        return new ResponseEntity<>(last30Days, HttpStatus.OK);
    }

}
