package com.example.onlinetesting.service.Code.Java;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RunResult;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.service.Analyzer.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface JavaService {
    String runJavaCode(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException;

    String runUserJavaCode(UserCode code, ArrayList<?> values, int userId) throws IOException;

    List<Variable> fetchJavaCodeVar(CodeRequest codeRequest) throws IOException;

    List<Variable> fetchUserJavaCodeVar(UserCode code) throws IOException;

    RunResult runJavaCodeExcel(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException;
}
