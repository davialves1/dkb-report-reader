package com.bank_statement_reader.dkb.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.service.CategoryMatcherService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMatcherService categoryMatcherService;

    @PostMapping("api/update-categories")
    public ResponseEntity<List<TransactionDto>> updateCategories() {
        List<TransactionDto> transactionDtos = categoryMatcherService.updateCategories();
        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

}
