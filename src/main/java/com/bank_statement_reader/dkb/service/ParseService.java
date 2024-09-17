package com.bank_statement_reader.dkb.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.text.NumberFormat;
import java.text.ParseException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bank_statement_reader.dkb.dto.FileResponseDto;
import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Balance;
import com.bank_statement_reader.dkb.entity.Transaction;
import com.bank_statement_reader.dkb.repository.BalanceRepository;
import com.bank_statement_reader.dkb.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParseService {

    private final TransactionService transactionService;

    private final TransactionRepository transactionRepository;

    private final CategoryMatcherService categoryMatcherService;

    private final BalanceRepository balanceRepository;

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

    public String removeEmptyLines(String csvContent) {
        return csvContent.lines()
                .filter(line -> !line.trim().isEmpty()) // Remove empty lines
                .collect(Collectors.joining(System.lineSeparator())); // Join lines back together
    }

    public String readCsvFile(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8); // Read CSV file into a String
    }

    public String cleanSpecialCharacters(String csvContent) {
        return csvContent.replaceAll("[^\\x00-\\x7F]", ""); // Remove non-ASCII characters
    }

    public String normalizeCharacters(String csvContent) {
        return csvContent.replaceAll("„", "\"").replaceAll("“", "\""); // Normalize quotes or other special characters
    }

    public String standardizeDelimiter(String csvContent, String desiredDelimiter) {
        // Assuming current delimiter is inconsistent, you can replace commas with
        // semicolons or vice versa
        return csvContent.replaceAll(",", desiredDelimiter); // Example: replace commas with semicolons
    }

    public String cleanCsvContent(String csvContent) {
        // Step 1: Remove non-ASCII special characters
        csvContent = cleanSpecialCharacters(csvContent);

        // Step 2: Normalize quotes or any other special characters
        csvContent = normalizeCharacters(csvContent);

        // Step 3: Remove empty or malformed lines
        csvContent = removeEmptyLines(csvContent);

        // Step 4: Standardize the delimiter (example: change to semicolon)
        csvContent = standardizeDelimiter(csvContent, ";");

        return csvContent;
    }

    public CSVParser parseCleanedCsv(MultipartFile file, String delimiter) throws IOException {
        // Step 1: Read the CSV content from the file
        String csvContent = readCsvFile(file);

        // Step 2: Clean the CSV content
        String cleanedCsvContent = cleanCsvContent(csvContent);

        // Step 3: Create a reader for the cleaned content
        Reader reader = new StringReader(cleanedCsvContent);

        // Step 4: Parse the cleaned CSV content
        return CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter.charAt(0))
                .setQuote('"')
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setTrim(true)
                .build()
                .parse(reader);
    }

    public CSVParser parseCSV(Reader reader, String delimiter) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter.charAt(0)) // Set the delimiter (comma or semicolon)
                .setQuote('"') // Set the quote character
                .setHeader() // Expect the header row
                .setSkipHeaderRecord(true) // Skip the header row while reading
                .setIgnoreSurroundingSpaces(true) // Ignore spaces around fields
                .setIgnoreEmptyLines(true) // Ignore empty lines
                .setTrim(true) // Trim fields
                .build()
                .parse(reader); // Parse the reader
    }

    public FileResponseDto parseToFileResponseDto(CSVParser csvParser, String fileName,
            FileResponseDto fileResponseDto) throws ParseException {
        List<Transaction> transactions = new ArrayList<>();
        int index = 0;
        for (CSVRecord record : csvParser) {
            if (index == 1) {
                List<String> cellList = record.toList();
                String stringDate = cellList.get(0).replace("Kontostand vom ", "").replace(":", "");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate localDate = LocalDate.parse(stringDate, formatter);
                NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
                Float balanceNumber = format.parse(cellList.get(1).replace(" €", "")).floatValue();
                Balance balance = new Balance();
                balance.setBalance(balanceNumber);
                balance.setUpdate(localDate);
                System.out.println(balance);
                balanceRepository.save(balance);
            }
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
                System.out.println("\u001B[32m" + "Duplicated Transaction");
                System.out.println("=========================> Skipping: " + t.getDescription() + "\u001B[0m");
            }
        });
        return fileResponseDto;
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
                transaction.setOriginalValue(transaction.getOriginalValue().concat("," + cell));

            }
            switch (index) {
                case 0:
                    LocalDate localDate = LocalDate.parse(cell, formatter);
                    transaction.setBookingDate(localDate);
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
