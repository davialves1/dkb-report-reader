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

    Pattern restaurantPattern = Pattern.compile(
            "(ristorante|restaurant|sultana|mutter habenicht|ox u\\.s\\. steakhouse|ristorante vivaldi la serenissima|restaurant tandure|troja|india house|hanayuki sushi restaurant|sakana sushi|brasserie & cocktailbar siebenschlafer|das alte haus|corvin's burger & beer|bollywood chili|entenhaus asia restaurant|al duomo|soshe restaurant & bar|pivbar|ristorante il punto|das pizzawerk|badsha|smira bbq restaurant|l'oliveto ristorante - pizzeria|quang anh|block house braunschweig|restaurant buzbag|salentino pizzeria & bistro|vielharmonie|cafe strupait|schadts brauerei gasthaus|makery|meier's gourmet cafe|nem quan|miner's coffee|wolfs gasthaus|zucker restaurant|braunschweiger parlament)",
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
        Matcher restaurantMatcher = restaurantPattern.matcher(description);
        if (restaurantMatcher.find()) {
            return CategoryEnum.RESTAURANT.getValue();
        }
        return CategoryEnum.MISCELLANEOUS.getValue();
    }
}
