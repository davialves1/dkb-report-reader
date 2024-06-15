package com.bank_statement_reader.dkb.controller;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bank_statement_reader.dkb.dto.FileResponseDto;
import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.repository.TransactionRepository;
import com.bank_statement_reader.dkb.service.CategoryMatcherService;
import com.bank_statement_reader.dkb.service.TransactionService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UploadController {

    private CategoryMatcherService categoryMatcherService;

    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    private static final String FILEPATH_STRING = System.getProperty("java.io.tmpdir");

    private static final List<String> columns = List.of(
            "Buchungsdatum",
            "Wertstellung",
            "Status",
            "Zahlungspflichtige*r",
            "Zahlungsempfänger*in",
            "Verwendungszweck",
            "Umsatztyp",
            "IBAN",
            "Betrag (€)",
            "Gläubiger-ID",
            "Mandatsreferenz",
            "Kundenreferenz");

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/api/upload")
    public ResponseEntity<FileResponseDto> upload(@RequestParam MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        FileResponseDto fileResponseDto = new FileResponseDto(fileName, new ArrayList<>());
        if (fileName != null && !fileName.contains(".csv")) {
            return new ResponseEntity<>(fileResponseDto, HttpStatus.BAD_REQUEST);
        }

        Path filePath = saveFileToTempFolder(file);

        try (Reader reader = Files.newBufferedReader(filePath);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                        .setDelimiter(';') // Assuming semicolon delimiter
                        .setQuote('"') // Assuming double quotes as encapsulation
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreSurroundingSpaces(true)
                        .setIgnoreEmptyLines(true)
                        .setTrim(true)
                        .build())) {

            List<Transaction> transactions = new ArrayList<>();
            int index = 0;
            for (CSVRecord record : csvParser) {
                if (index > 6) {
                    transactions.add(createTransactionDto(record.toList()));
                }
                index++;
            }
            transactions.removeIf(t -> t.getAmount() == null);
            transactions.forEach(t -> {
                Transaction isDuplicate = transactionRepository.findByOriginalValue(t.getOriginalValue());
                if (isDuplicate == null) {
                    Transaction transaction = transactionRepository.save(t);
                    TransactionDto transactionDto = transactionService.convertToDto(transaction);
                    fileResponseDto.transactions.add(transactionDto);
                } else {
                    System.out.println(
                            "Duplicated Transaction =========================> Skipping");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new FileResponseDto("Failed to process file: " + e.getMessage(), new ArrayList<>()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        transactionService.findAllTransactions().forEach(t -> fileResponseDto.transactions.add(t));
        return new ResponseEntity<FileResponseDto>(fileResponseDto, HttpStatus.OK);
    }

    private Path saveFileToTempFolder(MultipartFile file) throws Exception, IOException {
        Path uploadPath = Paths.get(FILEPATH_STRING);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        byte[] bytes = file.getBytes();
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        try {
            Files.write(filePath, bytes);
        } catch (IOException e) {
            System.out.println(e);
        }
        return filePath;
    }

    private Transaction createTransactionDto(List<String> row) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        Transaction transaction = new Transaction();
        int index = 0;
        for (String cell : row) {
            if (columns.contains(cell)) {
                continue;
            }
            if (transaction.getOriginalValue() == null) {
                transaction.setOriginalValue(cell);
            } else {
                transaction.setOriginalValue(transaction.getOriginalValue().concat(cell));

            }
            switch (index) {
                case 0:
                    transaction.setBookingDate(cell);
                    LocalDate localDate = LocalDate.parse(cell, formatter);
                    transaction.setDay(localDate.getDayOfMonth());
                    transaction.setMonth(localDate.getMonthValue());
                    transaction.setYear(localDate.getYear());
                    break;
                case 1:
                    transaction.setValueDate(cell);
                    break;
                case 2:
                    transaction.setStatus(cell);
                    break;
                case 3:
                    transaction.setPayer(cell);
                    break;
                case 4:
                    String category = categoryMatcherService.getCategory(cell);
                    transaction.setCategory(category);
                    transaction.setDescription(cell);
                    break;
                case 5:
                    transaction.setPurpose(cell);
                    break;
                case 6:
                    transaction.setType(cell);
                    break;
                case 7:
                    transaction.setIban(cell);
                    break;
                case 8:
                    transaction.setAmount(format.parse(cell).doubleValue());
                    break;
                case 9:
                    transaction.setCreditorId(cell);
                    break;
                case 10:
                    transaction.setMandateReference(cell);
                    break;
                case 11:
                    transaction.setCustomerReference(cell);
                    break;
            }
            index++;
        }
        return transaction;
    }

}
