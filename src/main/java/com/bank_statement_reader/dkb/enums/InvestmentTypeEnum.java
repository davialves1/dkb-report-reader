package com.bank_statement_reader.dkb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvestmentTypeEnum {
    SP500("S&P 500"),
    QQQ("QQQ");


    private final String value;
    
}
