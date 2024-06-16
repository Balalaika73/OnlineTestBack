package com.example.onlinetesting.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ExcelRequest {
    MultipartFile file;
    private CodeRequest codeRequest;
}