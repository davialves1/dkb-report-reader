package com.bank_statement_reader.dkb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvestmentUpdateDto {

    private String amount;

    private String entryDate;

    private String medium;

    private String type;

    private String familyMember;

    private String informationType;
    
}
