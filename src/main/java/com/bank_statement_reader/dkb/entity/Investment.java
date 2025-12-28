package com.bank_statement_reader.dkb.entity;

import java.time.LocalDate;


import com.bank_statement_reader.dkb.enums.InvestmentMediumEnum;
import com.bank_statement_reader.dkb.enums.InvestmentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    private InvestmentMediumEnum medium;
    
    @Enumerated(EnumType.STRING)
    private InvestmentTypeEnum type;
    
    private String informationType;
}
