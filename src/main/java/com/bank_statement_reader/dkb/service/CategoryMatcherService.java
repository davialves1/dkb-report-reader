package com.bank_statement_reader.dkb.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.bank_statement_reader.dkb.enums.CategoryEnum;

@Service
public class CategoryMatcherService {

    Pattern groceriesPattern = Pattern.compile("(edeka|aldi|lidl|rewe)", Pattern.CASE_INSENSITIVE);

    Pattern carPattern = Pattern.compile("(tierpark|aral station|tankstelle|volkswagen|pollux)",
            Pattern.CASE_INSENSITIVE);

    public String getCategory(String description) {
        Matcher groceriesMatcher = groceriesPattern.matcher(description);
        if (groceriesMatcher.find()) {
            return CategoryEnum.GROCERIES.getValue();
        }
        Matcher carMatcher = carPattern.matcher(description);
        if (carMatcher.find()) {
            return CategoryEnum.CAR.getValue();
        }
        return CategoryEnum.MISCELLANEOUS.getValue();
    }
}
