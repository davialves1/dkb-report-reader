package com.bank_statement_reader.dkb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bank_statement_reader.dkb.dto.FileResponseDto;

@RestController
public class UploadController {

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/api/upload")
    public ResponseEntity<FileResponseDto> upload(@RequestParam MultipartFile file) {
        FileResponseDto fileResponseDto = new FileResponseDto(file.getOriginalFilename());
        return new ResponseEntity<FileResponseDto>(fileResponseDto, HttpStatus.OK);
    }

}
