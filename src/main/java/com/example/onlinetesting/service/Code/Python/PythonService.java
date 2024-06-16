package com.example.onlinetesting.service.Code.Python;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RunResult;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.service.Analyzer.Variable;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface PythonService {
    String runPythonCode(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException;

    String runUserPythonCode(UserCode userCode, ArrayList<?> values, int userId) throws IOException;

    List<Variable> fetchPythonCodeVar(CodeRequest codeRequest) throws IOException;


    List<Variable> fetchUserPythonCodeVar(UserCode code) throws IOException;

    RunResult runPythonCodeExcel(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException;

    RunResult runUserPythonCodeExcel(UserCode userCode, ArrayList<?> values, int userId) throws IOException;
}
