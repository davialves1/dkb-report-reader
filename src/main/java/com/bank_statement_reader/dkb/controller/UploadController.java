package com.bank_statement_reader.dkb.controller;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
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

@RestController
public class UploadController {

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

            List<TransactionDto> transactionDtos = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                transactionDtos.add(createTransactionDto(record.toList()));
            }
            fileResponseDto.setTransactions(transactionDtos);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new FileResponseDto("Failed to process file: " + e.getMessage(), new ArrayList<>()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

    private TransactionDto createTransactionDto(List<String> row) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        TransactionDto transactionDto = new TransactionDto();
        int index = 0;
        for (String cell : row) {
            if (columns.contains(cell)) {
                continue;
            }
            switch (index) {
                case 0:
                    transactionDto.setBookingDate(cell);
                    break;
                case 1:
                    transactionDto.setValueDate(cell);
                    break;
                case 2:
                    transactionDto.setStatus(cell);
                    break;
                case 3:
                    transactionDto.setPayer(cell);
                    break;
                case 4:
                    transactionDto.setPayee(cell);
                    break;
                case 5:
                    transactionDto.setPurpose(cell);
                    break;
                case 6:
                    transactionDto.setType(cell);
                    break;
                case 7:
                    transactionDto.setIban(cell);
                    break;
                case 8:
                    transactionDto.setAmount(format.parse(cell).doubleValue());
                    break;
                case 9:
                    transactionDto.setCreditorId(cell);
                    break;
                case 10:
                    transactionDto.setMandateReference(cell);
                    break;
                case 11:
                    transactionDto.setCustomerReference(cell);
                    break;
            }
            index++;
        }
        return transactionDto;
    }

}
