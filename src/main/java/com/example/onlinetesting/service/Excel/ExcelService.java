package com.example.onlinetesting.service.Excel;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.ExcelFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelService {
    byte[] createUserExcelFilePython(UserCode userCode) throws IOException;

    public abstract byte[] createExcelFilePython(CodeRequest codeRequest) throws IOException;

    byte[] uploadPythonFile(MultipartFile file, int id, CodeRequest codeRequest) throws IOException;

    byte[] uploadUserPythonFile(MultipartFile file, int userId, UserCode userCode) throws IOException;

    byte[] createExcelFileJava(CodeRequest codeRequest) throws IOException;

    byte[] uploadJavaFile(MultipartFile file, int userId, CodeRequest codeRequest) throws IOException;
}
