package com.example.onlinetesting.service.Code;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CodeService {
    String fetchCodeFromGitHub(CodeRequest codeRequest) throws IOException;

    String fetchUserCode(UserCode userCode) throws IOException;

    Map<String, Integer> getTestsCountByLast3Months();

    Map<String, Integer> getUserTestsCountByLast3Months(int userId);

    List<Test> getTestsHistory(int userId);
}
