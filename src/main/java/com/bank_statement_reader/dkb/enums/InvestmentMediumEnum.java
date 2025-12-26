package com.bank_statement_reader.dkb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvestmentMediumEnum {
    DKB("DKB"),
    TRADE_REPUBLIC("Trade Republic"),
    E_TORO("eToro");
    
    private final String value;
}
