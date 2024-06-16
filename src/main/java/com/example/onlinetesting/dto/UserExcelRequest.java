package com.example.onlinetesting.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserExcelRequest {
    MultipartFile file;
    private UserCode userCode;
}
