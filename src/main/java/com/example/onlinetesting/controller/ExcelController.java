package com.example.onlinetesting.controller;

import com.example.onlinetesting.dto.*;
import com.example.onlinetesting.service.Excel.ExcelService;
import com.example.onlinetesting.service.JWT.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel")
@CrossOrigin
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelService excelService;
    private final JWTService jwtService;
    private final HttpServletRequest request;

    @PostMapping("/downloadVarsPython")
    public ResponseEntity<?> downloadVarsPython(@RequestBody CodeRequest codeRequest, @RequestHeader("Authorization") String token) throws IOException {
        excelService.createExcelFilePython(codeRequest);
        byte[] fileData=excelService.createExcelFilePython(codeRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileData);
    }

    @PostMapping("/downloadUserVarsPython")
    public ResponseEntity<?> downloadUserVarsPython(@RequestBody UserCode userCode, @RequestHeader("Authorization") String token) throws IOException {
        byte[] fileData=excelService.createUserExcelFilePython(userCode);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileData);
    }

    @PostMapping("/downloadVarsJava")
    public ResponseEntity<?> downloadVarsJava(@RequestBody CodeRequest codeRequest, @RequestHeader("Authorization") String token) throws IOException {
        excelService.createExcelFilePython(codeRequest);
        byte[] fileData=excelService.createExcelFileJava(codeRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileData);
    }

    @PostMapping("/uploudVarsPython")
    public ResponseEntity<?> uploadVarsPython(@ModelAttribute ExcelRequest excelRequest) throws IOException {
        try {
            String token = request.getHeader("Authorization");
            token = token.substring(7);
            int userId = jwtService.extractUserId(token);
            byte[] uploadedFileName = excelService.uploadPythonFile(excelRequest.getFile(),userId, excelRequest.getCodeRequest());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(uploadedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @PostMapping("/uploudUserVarsPython")
    public ResponseEntity<?> uploadVarsPython(@ModelAttribute UserExcelRequest excelRequest) throws IOException {
        try {
            String token = request.getHeader("Authorization");
            token = token.substring(7);
            int userId = jwtService.extractUserId(token);
            byte[] uploadedFileName = excelService.uploadUserPythonFile(excelRequest.getFile(),userId, excelRequest.getUserCode());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(uploadedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @PostMapping("/uploudVarsJava")
    public ResponseEntity<?> uploadVarsJava(@ModelAttribute ExcelRequest excelRequest) throws IOException {
        try {
            String token = request.getHeader("Authorization");
            token = token.substring(7);
            int userId = jwtService.extractUserId(token);
            byte[] uploadedFileName = excelService.uploadJavaFile(excelRequest.getFile(),userId, excelRequest.getCodeRequest());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(uploadedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }
}
