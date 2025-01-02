package com.bank_statement_reader.dkb.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bank_statement_reader.dkb.dto.FileResponseDto;
import com.bank_statement_reader.dkb.dto.TransactionDto;
import com.bank_statement_reader.dkb.entity.Balance;
import com.bank_statement_reader.dkb.repository.BalanceRepository;
import com.bank_statement_reader.dkb.service.ParseService;
import com.bank_statement_reader.dkb.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final BalanceRepository balanceRepository;

    private final TransactionService transactionService;

    private final ParseService parseService;

    private static final String FILEPATH_STRING = System.getProperty("java.io.tmpdir");

    @GetMapping("/api")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("api/all-data")
    public ResponseEntity<List<TransactionDto>> getAllData() {
        List<TransactionDto> transactionDtos = transactionService.findAllTransactions();
        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    @GetMapping("api/balance")
    public ResponseEntity<Balance> getBalance() {
        Balance balance = balanceRepository.findFirstByOrderByUpdateDesc();
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping("/api/upload")
    public ResponseEntity<FileResponseDto> upload(@RequestParam MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        FileResponseDto fileResponseDto = new FileResponseDto(fileName, new ArrayList<>());
        if (fileName != null && !fileName.contains(".csv")) {
            return new ResponseEntity<>(fileResponseDto, HttpStatus.BAD_REQUEST);
        }

        Path filePath = saveFileToTempFolder(file);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            StringBuilder sanitizedContent = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                line = line.replace("\u00A0", " ").trim();
                line = line.replace("\";\"", "\",\"");
                sanitizedContent.append(line).append("\n");
            }
            System.out.println("sanitizedContent");
            System.out.println(sanitizedContent);
            CSVParser csvParser;
            try {
                System.out.println("First attempt with comma as delimiter");
                csvParser = this.parseService.parseCSV(new StringReader(sanitizedContent.toString()), ",");
            } catch (Exception e) {
                System.out.println("Failed first attempt with comma.");
                System.out.println("Close and reopen the file for the semicolon attempt");
                try (Reader secondReader = Files.newBufferedReader(filePath)) {
                    System.out.println("Second attempt with semicolon as delimiter");
                    csvParser = this.parseService.parseCSV(secondReader, ";");
                }
            }
            // Proceed with parsing after a successful attempt
            parseService.parseToFileResponseDto(csvParser, fileName, fileResponseDto);
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

}
