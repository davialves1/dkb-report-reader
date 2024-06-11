package com.bank_statement_reader.dkb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryEnum {
    GROCERIES("Groceries"),
    MISCELLANEOUS("Miscellaneous"),
    CAR("Car");

    private final String value;

}
