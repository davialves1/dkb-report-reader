package com.bank_statement_reader.dkb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.enums.CategoryEnum;
import com.bank_statement_reader.dkb.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryMatcherService {

        private final TransactionRepository transactionRepository;

        private final TransactionService transactionService;

        private final Map<Pattern, String> categoryPatterns = Map.ofEntries(
                        Map.entry(Pattern.compile("(edeka|aldi|lidl|rewe|flink|Supermarkt)", Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.GROCERIES.getValue()),
                        Map.entry(Pattern.compile("(tierpark|aral station|tankstelle|volkswagen|pollux|cleancar|Aral)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.CAR.getValue()),
                        Map.entry(Pattern.compile(
                                        "(ITALIAN\\.S|ristorante|restaurant|sultana|mutter habenicht|ox u\\.s\\. steakhouse|ristorante vivaldi la serenissima|restaurant tandure|troja|india house|hanayuki sushi restaurant|sakana sushi|brasserie & cocktailbar siebenschlafer|das alte haus|corvin's burger & beer|bollywood chili|entenhaus asia restaurant|al duomo|soshe restaurant & bar|pivbar|ristorante il punto|das pizzawerk|badsha|smira bbq restaurant|l'oliveto ristorante - pizzeria|quang anh|block house braunschweig|restaurant buzbag|salentino pizzeria & bistro|vielharmonie|cafe strupait|schadts brauerei gasthaus|makery|meier's gourmet cafe|nem quan|miner's coffee|wolfs gasthaus|zucker restaurant|braunschweiger parlament)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.RESTAURANT.getValue()),
                        Map.entry(Pattern.compile("(bauhaus|Beton2Gold|BS\\/ENERGY|getsafe|TaskRabbit)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.HOUSE.getValue()),
                        Map.entry(
                                        Pattern.compile("(amazon|dm-drogerie|temu|Weisses.Ross|ROSSMANN|tiger|apotheke)",
                                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.SHOPPING.getValue()),
                        Map.entry(Pattern.compile("(Bäckerei|Bäcker|Baeckerei|backWerk|Steinecke)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.BAKERY.getValue()),
                        Map.entry(Pattern.compile("(Lieferando|Lieferservice)", Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.DELIVERY.getValue()),
                        Map.entry(Pattern.compile("(Fachbereich Kinder|Krippe|Kristina Paulsen)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.EDUCATION.getValue()),
                        Map.entry(
                                        Pattern.compile("(Centerparcs|Center Parcs|ECP - Deutschland|Autostadt)",
                                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.LEISURE.getValue()),
                        Map.entry(Pattern.compile("(Kamilla Alvarenga Gomes Soutto|Family DKB)",
                                        Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.SALARY.getValue()),
                        Map.entry(Pattern.compile("(Kamilla Revolut|Davi Commerzbank)", Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.PERSONAL.getValue()),
                        Map.entry(Pattern.compile("(Vodafone|Telefonica)", Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.INTERNET.getValue()),
                        Map.entry(Pattern.compile("(32/ARAL\\.BRAUNSC)", Pattern.CASE_INSENSITIVE),
                                        CategoryEnum.WITHDRAW.getValue()));

        public String getCategory(String description) {
                for (Map.Entry<Pattern, String> entry : categoryPatterns.entrySet()) {
                        Matcher matcher = entry.getKey().matcher(description);
                        if (matcher.find()) {
                                return entry.getValue();
                        }
                }
                return CategoryEnum.MISCELLANEOUS.getValue();
        }

        public List<TransactionDto> updateCategories() {
                List<Transaction> transactionDtos = transactionRepository.findAll();
                for (Transaction transaction : transactionDtos) {
                        String updatedCategory = getCategory(transaction.getDescription());
                        transaction.setCategory(updatedCategory);
                        transactionRepository.save(transaction);
                }
                return transactionRepository.findTransactionsFrom(LocalDate.now().minusDays(30)).stream()
                                .map(t -> transactionService.convertToDto(t)).toList();
        }
}
