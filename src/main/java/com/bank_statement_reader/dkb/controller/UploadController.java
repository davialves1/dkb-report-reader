package com.bank_statement_reader.dkb.controller;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bank_statement_reader.dkb.dto.FileResponseDto;

@RestController
public class UploadController {

    private static final String FILEPATH_STRING = System.getProperty("java.io.tmpdir");

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/api/upload")
    public ResponseEntity<FileResponseDto> upload(@RequestParam MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        FileResponseDto fileResponseDto = new FileResponseDto(fileName);
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

            for (CSVRecord record : csvParser) {
                System.out.println(record.get(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new FileResponseDto("Failed to process file: " + e.getMessage()),
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

}
