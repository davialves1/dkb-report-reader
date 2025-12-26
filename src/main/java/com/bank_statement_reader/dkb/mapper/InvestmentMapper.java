package com.bank_statement_reader.dkb.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.bank_statement_reader.dkb.dto.InvestmentUpdateDto;
import com.bank_statement_reader.dkb.entity.Investment;
import com.bank_statement_reader.dkb.enums.InvestmentMediumEnum;
import com.bank_statement_reader.dkb.enums.InvestmentTypeEnum;

@Mapper
public interface InvestmentMapper {

    InvestmentMapper INSTANCE = Mappers.getMapper(InvestmentMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toDouble")
    @Mapping(target = "entryDate", source = "entryDate", qualifiedByName = "toLocalDate")
    @Mapping(target = "medium", source = "medium", qualifiedByName = "toMediumEnum")
    @Mapping(target = "type", source = "type", qualifiedByName = "toTypeEnum")
    Investment toEntity(InvestmentUpdateDto dto);

    @Named("toDouble")
    default Double toDouble(String s) {
        if (s == null || s.isBlank()) return null;
        return Double.valueOf(s.replace(",", "."));
    }

    @Named("toLocalDate")
    default LocalDate toLocalDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s); // expects yyyy-MM-dd
    }

    @Named("toMediumEnum")
    default InvestmentMediumEnum toMediumEnum(String s) {
        if (s == null || s.isBlank()) return null;
        return InvestmentMediumEnum.valueOf(s);
    }

    @Named("toTypeEnum")
    default InvestmentTypeEnum toTypeEnum(String s) {
        if (s == null || s.isBlank()) return null;
        return InvestmentTypeEnum.valueOf(s);
    }
}
