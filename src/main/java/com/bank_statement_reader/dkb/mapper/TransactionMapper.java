package com.bank_statement_reader.dkb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;

@Mapper
public interface TransactionMapper {

    TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    TransactionDto convertToDto(Transaction transaction);

    Transaction convertToToEntity(TransactionDto transactionDto);

}
