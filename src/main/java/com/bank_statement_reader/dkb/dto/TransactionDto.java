package com.bank_statement_reader.dkb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private String bookingDate;
    private String valueDate;
    private String status;
    private String payer;
    private String description;
    private String purpose;
    private String type;
    private String iban;
    private Double amount;
    private String creditorId;
    private String mandateReference;
    private String customerReference;
    private int day;
    private int month;
    private int year;
    private String category;

}
