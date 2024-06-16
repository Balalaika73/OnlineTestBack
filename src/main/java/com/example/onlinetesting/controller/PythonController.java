package com.example.onlinetesting.controller;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.PythonRunCode;
import com.example.onlinetesting.dto.UserRunCode;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.service.Analyzer.Variable;
import com.example.onlinetesting.service.Code.Python.PythonService;
import com.example.onlinetesting.service.JWT.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/code/python")
@CrossOrigin
@RequiredArgsConstructor
public class PythonController {
    private final PythonService pythonService;
    private final HttpServletRequest request;
    private final JWTService jwtService;

    @PostMapping("/runCode")
    public ResponseEntity<String> runCode(@RequestBody PythonRunCode pythonRunCode) throws IOException {
        CodeRequest codeRequest = new CodeRequest();
        codeRequest.setUrlCode(pythonRunCode.getUrl());

        // Получаем токен из заголовка запроса
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Удаляем "Bearer " из строки
        }
        int userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(pythonService.runPythonCode(codeRequest, pythonRunCode.getValues(), userId));
    }

    @PostMapping("/runUserCode")
    public ResponseEntity<String> runUserCode(@RequestBody UserRunCode userRunCode) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Удаляем "Bearer " из строки
        }
        int userId = jwtService.extractUserId(token);
        UserCode userCode = new UserCode();
        userCode.setCode(userRunCode.getCode());
        // Вызываем метод runPythonCode, передавая идентификатор пользователя
        return ResponseEntity.ok(pythonService.runUserPythonCode(userCode, userRunCode.getValues(), userId));
    }


    @PostMapping("/getVar")
    public ResponseEntity<List<Variable>> getVar(@RequestBody CodeRequest url) throws IOException {
        return ResponseEntity.ok(pythonService.fetchPythonCodeVar(url));
    }

    @PostMapping("/getUserVar")
    public ResponseEntity<List<Variable>> getUserVar(@RequestBody UserCode code) throws IOException {
        return ResponseEntity.ok(pythonService.fetchUserPythonCodeVar(code));
    }

}
