package com.bank_statement_reader.dkb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryEnum {
    GROCERIES("Groceries"),
    MISCELLANEOUS("Miscellaneous"),
    CAR("Car"),
    HOUSE("House"),
    SHOPPING("Shopping"),
    DELIVERY("Delivery"),
    BAKERY("Bakery"),
    RESTAURANT("Restaurant"),
    EDUCATION("Education"),
    WITHDRAW("Withdraw"),
    SALARY("Salary"),
    PERSONAL("Personal"),
    INTERNET("Internet"),
    LEISURE("Leisure"),
    SUBSCRIPTION("Subscription");

    private final String value;

}
