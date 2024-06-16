package com.example.onlinetesting.service.Code.Python;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RunResult;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.Test;
import com.example.onlinetesting.repository.TestRepository;
import com.example.onlinetesting.service.Analyzer.Variable;
import com.example.onlinetesting.service.Code.CodeService;
import com.example.onlinetesting.service.Analyzer.PythonCodeAnalyzer;
import lombok.RequiredArgsConstructor;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PythonServiceImpl implements PythonService{

    @Autowired
    private CodeService codeService;

    @Autowired
    private PythonCodeAnalyzer pythonCodeAnalyzer;

    private final TestRepository testRepository;


    public String runPythonCode(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException {
        String pythonScript = codeService.fetchCodeFromGitHub(codeRequest);

        Test test = new Test();
        test.setUrl(codeRequest.getUrlCode());
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));

        List<Variable> vars = fetchPythonCodeVar(codeRequest);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            String varType = var.getType();
            Object varValue = values.get(i);
            if (varType == "str") {
                varValue = "'" + values.get(i) + "'";
            }

            String replacement = varName + " = " + varValue.toString();

            String regex = "\\b" + varName + "\\s*=\\s*[^\\n]+";
            pythonScript = pythonScript.replaceAll(regex, replacement);
        }
        PythonInterpreter interpreter = new PythonInterpreter();
        StringWriter outputWriter = new StringWriter();
        interpreter.setOut(outputWriter);
        String output = "";
        try {
            interpreter.exec(pythonScript);
            output = outputWriter.toString();
            test.setResult(true);
            testRepository.save(test);
            return output;
        } catch (PyException e) {
            test.setResult(false);
            testRepository.save(test);
            return e.getMessage();
        }
    }

    @Override
    public String runUserPythonCode(UserCode code, ArrayList<?> values, int userId) throws IOException {
        String pythonScript = code.getCode();
        System.out.println("Hi" + pythonScript);

        Test test = new Test();
        test.setUrl("User's code");
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));

        List<Variable> vars = fetchUserPythonCodeVar(code);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            String varType = var.getType();
            Object varValue = values.get(i);
            if (varType == "str") {
                varValue = "'" + values.get(i) + "'";
            }

            String replacement = varName + " = " + varValue.toString();

            String regex = "\\b" + varName + "\\s*=\\s*[^\\n]+";
            pythonScript = pythonScript.replaceAll(regex, replacement);
        }
        PythonInterpreter interpreter = new PythonInterpreter();
        StringWriter outputWriter = new StringWriter();
        interpreter.setOut(outputWriter);
        String output = "";
        try {
            interpreter.exec(pythonScript);
            output = outputWriter.toString();
            test.setResult(true);
            testRepository.save(test);
            return output;
        } catch (PyException e) {
            test.setResult(false);
            testRepository.save(test);
            return e.getMessage();
        }
    }

    public List<Variable> fetchPythonCodeVar(CodeRequest codeRequest) throws IOException {
        String pythonCode = codeService.fetchCodeFromGitHub(codeRequest);
        List<Variable> variables = pythonCodeAnalyzer.findUserInputVariables(pythonCode);
        return variables;
    }

    @Override
    public List<Variable> fetchUserPythonCodeVar(UserCode code) throws IOException {
        List<Variable> variables = pythonCodeAnalyzer.findUserInputVariables(code.getCode());
        return variables;
    }

    @Override
    public RunResult runPythonCodeExcel(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException {
        String pythonScript = codeService.fetchCodeFromGitHub(codeRequest);

        Test test = new Test();
        test.setUrl(codeRequest.getUrlCode());
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));

        List<Variable> vars = fetchPythonCodeVar(codeRequest);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            String varType = var.getType();
            Object varValue = values.get(i);
            if (varType == "str") {
                varValue = "'" + values.get(i) + "'";
            }

            String replacement = varName + " = " + varValue.toString();

            String regex = "\\b" + varName + "\\s*=\\s*[^\\n]+";
            pythonScript = pythonScript.replaceAll(regex, replacement);
        }
        StringWriter outputWriter = new StringWriter();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.setOut(outputWriter);

        try {
            interpreter.exec(pythonScript);
            test.setResult(true);
            testRepository.save(test);
            return new RunResult(outputWriter.toString(), true);
        } catch (PyException e) {
            test.setResult(false);
            testRepository.save(test);
            return new RunResult(outputWriter.toString(), false);
        }
    }

    @Override
    public RunResult runUserPythonCodeExcel(UserCode userCode, ArrayList<?> values, int userId) throws IOException {
        String pythonScript = userCode.getCode();

        Test test = new Test();
        test.setUrl("User's code");
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));

        List<Variable> vars = fetchUserPythonCodeVar(userCode);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            String varType = var.getType();
            Object varValue = values.get(i);
            if (varType == "str") {
                varValue = "'" + values.get(i) + "'";
            }

            String replacement = varName + " = " + varValue.toString();

            String regex = "\\b" + varName + "\\s*=\\s*[^\\n]+";
            pythonScript = pythonScript.replaceAll(regex, replacement);
        }
        StringWriter outputWriter = new StringWriter();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.setOut(outputWriter);

        try {
            interpreter.exec(pythonScript);
            test.setResult(true);
            testRepository.save(test);
            return new RunResult(outputWriter.toString(), true);
        } catch (PyException e) {
            test.setResult(false);
            testRepository.save(test);
            return new RunResult(outputWriter.toString(), false);
        }
    }
}
