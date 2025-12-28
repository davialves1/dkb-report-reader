package com.bank_statement_reader.dkb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bank_statement_reader.dkb.dto.InvestmentUpdateDto;
import com.bank_statement_reader.dkb.entity.Investment;
import com.bank_statement_reader.dkb.service.InvestmentService;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping("/api/investment")
    public Investment addInvestmentUpdate(@RequestBody InvestmentUpdateDto investmentUpdateDto) {
        return this.investmentService.create(investmentUpdateDto);
    }
    
    @GetMapping("/api/investment")
    public List<Investment> getAll() {
        return this.investmentService.getAll();
        
    }
    
    
}
