package com.bank_statement_reader.dkb.entity;

import java.time.LocalDate;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    @Column(unique = true, length = 2000)
    private String originalValue;
    @Column(length = 1000)
    private String description;
    private LocalDate bookingDate;
    private String valueDate;
    private String status;
    private String payer;
    @Column(length = 1000)
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
