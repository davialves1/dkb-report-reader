package com.bank_statement_reader.dkb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank_statement_reader.dkb.dto.InvestmentUpdateDto;
import com.bank_statement_reader.dkb.entity.Investment;
import com.bank_statement_reader.dkb.mapper.InvestmentMapper;
import com.bank_statement_reader.dkb.repository.InvestmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    public Investment create(InvestmentUpdateDto dto) {
        Investment entity = InvestmentMapper.INSTANCE.toEntity(dto);
        return this.investmentRepository.save(entity);
    }

    public List<Investment> getAll() {
        return this.investmentRepository.findAll();
    }
    
}
