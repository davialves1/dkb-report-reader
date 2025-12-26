package com.bank_statement_reader.dkb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bank_statement_reader.dkb.dto.InvestmentUpdateDto;
import com.bank_statement_reader.dkb.entity.Investment;
import com.bank_statement_reader.dkb.service.InvestmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping("/api/investment")
    public Investment addInvestmentUpdate(@RequestBody InvestmentUpdateDto investmentUpdateDto) {
        Investment entity = this.investmentService.create(investmentUpdateDto);
        return entity;
    }
    
    
}
