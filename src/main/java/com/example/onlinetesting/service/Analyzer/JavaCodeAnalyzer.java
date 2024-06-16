package com.example.onlinetesting.service.Analyzer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JavaCodeAnalyzer {

    public static String hasScannerAssignment(String code) {
        // Регулярное выражение для поиска присвоения Scanner переменной
        String regex = "\\b(?:Scanner)\\s+([a-zA-Z][\\w]*)\\s*=\\s*new\\s+Scanner\\(System\\.in\\);";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        // Возвращаем true, если найдено совпадение
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static List<Variable> findUserInputVariables(String code) {
        List<Variable> inputVariables = new ArrayList<>();

        // Разбиваем код на строки
        String[] lines = code.split("\n");

        // Проходим по каждой строке кода
        for (String line : lines) {
            // Если строка содержит вызов метода Scanner для ввода значения
            if (line.contains(".nextInt()") || line.contains(".next()") || line.contains(".nextLine()")) {
                // Получаем тип переменной (первое слово строки)
                String type = line.trim().split("\\s+")[0];
                // Получаем имя переменной (второе слово строки)
                String name = line.trim().split("\\s+")[1].split("=")[0];
                // Добавляем переменную, ее тип и имя в список
                inputVariables.add(new Variable(type, name));
            }
        }

        return inputVariables;
    }





}
