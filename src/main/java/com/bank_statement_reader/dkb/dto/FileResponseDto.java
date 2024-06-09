package com.bank_statement_reader.dkb.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseDto {

    public String fileName;

    public List<TransactionDto> transactions;

}
