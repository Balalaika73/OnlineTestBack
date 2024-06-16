package com.example.onlinetesting.controller;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RepositoryRequest;
import com.example.onlinetesting.dto.TestResultSummary;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.Repo;
import com.example.onlinetesting.enteties.Test;
import com.example.onlinetesting.service.Code.CodeService;
import com.example.onlinetesting.service.JWT.JWTService;
import com.example.onlinetesting.service.RepoFiles.RepoFilesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/code")
@CrossOrigin
@RequiredArgsConstructor
public class CodeController {
    private final RepoFilesService repoFilesService;
    private final CodeService codeService;
    private final HttpServletRequest request;
    private final JWTService jwtService;
    @PostMapping("/getRepoFiles")
    public ResponseEntity<List<String>> getRepositoryFiles(@RequestBody RepositoryRequest repositoryRequest) {
        return ResponseEntity.ok(repoFilesService.getFilesNames(repositoryRequest));
    }
    @PostMapping("/getCode")
    public ResponseEntity<String> getCode(@RequestBody CodeRequest url) throws IOException {
        return ResponseEntity.ok(codeService.fetchCodeFromGitHub(url));
    }

    @PostMapping("/getUserCode")
    public ResponseEntity<String> getUserCode(@RequestBody UserCode code) throws IOException {
        return ResponseEntity.ok(codeService.fetchUserCode(code));
    }

    @GetMapping("/countThreeMonths")
    public ResponseEntity<Map<String, Integer>> getTestsCountByLast3Months() {
        Map<String, Integer> testsCountByMonth = codeService.getTestsCountByLast3Months();
        return ResponseEntity.ok(testsCountByMonth);
    }

    @GetMapping("/countUserTestsThreeMonths")
    public ResponseEntity<Map<String, Integer>> getUserTestsCountByLast3Months() {
        String token = request.getHeader("Authorization");
        int userId = 0;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtService.extractUserId(token);
        }
        Map<String, Integer> testsCountByMonth = codeService.getUserTestsCountByLast3Months(userId);
        return ResponseEntity.ok(testsCountByMonth);
    }

    @GetMapping("/getTestsHistory")
    public ResponseEntity<List<Test>> getTestsHistory() {
        String token = request.getHeader("Authorization");
        int userId = 0;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtService.extractUserId(token);
        }
        List<Test> history = codeService.getTestsHistory(userId);
        for (Test test : history) {
            String url = test.getUrl();
            url = url.replace("raw.githubusercontent.com", "github.com").replace("/main/", "/blob/main/");
            test.setUrl(url);
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/getTestResults")
    public ResponseEntity<TestResultSummary> getTestResults() {
        String token = request.getHeader("Authorization");
        int userId = 0;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtService.extractUserId(token);
        }

        List<Test> history = codeService.getTestsHistory(userId);

        // Подсчет числа положительных и отрицательных результатов
        int positiveResults = 0;
        int negativeResults = 0;
        for (Test test : history) {
            if (test.isResult()) {
                positiveResults++;
            } else {
                negativeResults++;
            }
        }

        TestResultSummary resultSummary = new TestResultSummary(positiveResults, negativeResults);
        return ResponseEntity.ok(resultSummary);
    }
}
