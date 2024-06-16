package com.example.onlinetesting.service.Analyzer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PythonCodeAnalyzer {

    public List<Variable> findUserInputVariables(String pythonCode) {
        List<Variable> userInputVariables = new ArrayList<>();

        // Паттерн для поиска строк, содержащих функции ввода
        Pattern pattern = Pattern.compile(".*(input|raw_input|prompt)\\(");

        // Разбиваем код Python на строки
        String[] lines = pythonCode.split("\n");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String variableName = extractVariableName(line); // Извлекаем имя переменной
                String variableType = determineVariableType(line); // Определяем тип переменной
                userInputVariables.add(new Variable(variableType, variableName)); // Добавляем тип и имя в список
            } else if (!line.trim().startsWith("#")) {
                // Если строка не является комментарием и не содержит функции ввода, игнорируем её
                continue;
            }
        }

        return userInputVariables;
    }

    private String extractVariableName(String line) {
        String[] words = line.trim().split("\\s+");
        if (words.length > 0) {
            return words[0];
        } else {
            return "";
        }
    }



    private String determineVariableType(String line) {
        if (line.contains("int(")) {
            return "int";
        } else if (line.contains("float(")){
            return "float";
        } else if (line.contains("bool(")) {
            return "bool";
        } else {
            return "str";
        }
    }
}