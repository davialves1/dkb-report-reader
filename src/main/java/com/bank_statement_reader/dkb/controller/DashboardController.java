package com.bank_statement_reader.dkb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;

    @GetMapping("/api/dashboard/{days}")
    public ResponseEntity<List<TransactionDto>> getDashboard(
            @PathVariable() String days) {
        List<TransactionDto> previous = transactionService.getPrevious(Integer.parseInt(days));
        return new ResponseEntity<>(previous, HttpStatus.OK);
    }

    @GetMapping("/api/dashboard/monthly/{year}")
    public ResponseEntity<HashMap<Integer, List<TransactionDto>>> getByMonth(@PathVariable() String year) {
        HashMap<Integer, List<TransactionDto>> transactionsByMonth = new HashMap<>();
        List<TransactionDto> transactionDtos = year.isBlank() ? transactionDtos = transactionService.findAllFromCurrentYear() : transactionService.findByYear(Integer.parseInt(year));
       
        for (TransactionDto transactionDto : transactionDtos) {
            Integer month = transactionDto.getMonth();
            List<TransactionDto> accumulated = transactionsByMonth.get(month);
            if (accumulated == null) {
                List<TransactionDto> newList = new ArrayList<TransactionDto>();
                newList.add(transactionDto);
                transactionsByMonth.put(month, newList);
            } else {
                accumulated.add(transactionDto);
            }
        }
        return new ResponseEntity<>(transactionsByMonth, HttpStatus.OK);
    }

}
