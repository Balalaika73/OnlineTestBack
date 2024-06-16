package com.example.onlinetesting.service.Code.Java;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RunResult;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.Test;
import com.example.onlinetesting.repository.TestRepository;
import com.example.onlinetesting.service.Analyzer.JavaCodeAnalyzer;
import com.example.onlinetesting.service.Analyzer.Variable;
import com.example.onlinetesting.service.Code.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JavaServiceImpl implements JavaService{
    @Autowired
    private CodeService codeService;

    @Autowired
    private JavaCodeAnalyzer javaCodeAnalyzer;

    private final TestRepository testRepository;

    @Override
    public String runJavaCode(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException {
        String javaScript = codeService.fetchCodeFromGitHub(codeRequest);

        Test test = new Test();
        test.setUrl(codeRequest.getUrlCode());
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));
        System.out.println(test);

        List<Variable> vars = fetchJavaCodeVar(codeRequest);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            Object varValue = values.get(i);

            String replacement;
            if ("string".equalsIgnoreCase(var.getType())) {
                replacement = varName + " = \"" + varValue.toString() + "\";";
            } else {
                replacement = varName + " = " + varValue.toString() + ";";
            }
            String regex = "\\b" + varName + "\\s*=\\s*(?:.*?(?:\\.nextInt\\(\\)|\\.next\\(\\)|\\.nextLine\\(\\))[^\\n]*)+";
            javaScript = javaScript.replaceAll(regex, replacement);

        }

        try {
            Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*\\{");
            Matcher matcher = pattern.matcher(javaScript);
            String className;
            if (matcher.find()) {
                className = matcher.group(1);
            } else {
                throw new IllegalArgumentException("Не удалось определить имя класса в коде");
            }

            // Получаем экземпляр Java компилятора
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // Получаем стандартный объект FileManager
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            // Создаем и компилируем исходный файл из строки
            JavaFileObject javaFile = new JavaSourceFromString(className, javaScript);
            Iterable<? extends JavaFileObject> compilationUnits = List.of(javaFile);
            compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

            // Закрываем FileManager
            fileManager.close();

            // Создаем загрузчик классов
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(".").toURI().toURL()});

            // Загружаем класс
            Class<?> loadedClass = classLoader.loadClass(className);
            // Находим метод main и вызываем его
            Method method = loadedClass.getMethod("main", String[].class);

            // Запускаем метод main и перенаправляем вывод в ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(outputStream));
            method.invoke(null, new Object[]{null});
            test.setResult(true);

            // Возвращаем результат выполнения программы
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            test.setResult(false);
            return e.getMessage();
        } finally {
            testRepository.save(test);
        }
    }

    @Override
    public String runUserJavaCode(UserCode code, ArrayList<?> values, int userId) throws IOException {
        String javaScript = code.getCode();

        Test test = new Test();
        test.setUrl("User's code");
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));

        List<Variable> vars = fetchUserJavaCodeVar(code);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            Object varValue = values.get(i);

            String replacement;
            if ("string".equalsIgnoreCase(var.getType())) {
                replacement = varName + " = \"" + varValue.toString() + "\";";
            } else {
                replacement = varName + " = " + varValue.toString() + ";";
            }
            String regex = "\\b" + varName + "\\s*=\\s*(?:.*?(?:\\.nextInt\\(\\)|\\.next\\(\\)|\\.nextLine\\(\\))[^\\n]*)+";
            javaScript = javaScript.replaceAll(regex, replacement);
            System.out.println(javaScript);
        }
        try {
            Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*\\{");
            Matcher matcher = pattern.matcher(javaScript);
            String className;
            if (matcher.find()) {
                className = matcher.group(1);
            } else {
                throw new IllegalArgumentException("Не удалось определить имя класса в коде");
            }

            // Получаем экземпляр Java компилятора
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // Получаем стандартный объект FileManager
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            // Создаем и компилируем исходный файл из строки
            JavaFileObject javaFile = new JavaSourceFromString(className, javaScript);
            Iterable<? extends JavaFileObject> compilationUnits = List.of(javaFile);
            compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

            // Закрываем FileManager
            fileManager.close();

            // Создаем загрузчик классов
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(".").toURI().toURL()});

            // Загружаем класс
            Class<?> loadedClass = classLoader.loadClass(className);
            // Находим метод main и вызываем его
            Method method = loadedClass.getMethod("main", String[].class);

            // Запускаем метод main и перенаправляем вывод в ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(outputStream));
            method.invoke(null, new Object[]{null});
            test.setResult(true);

            // Возвращаем результат выполнения программы
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            test.setResult(false);
            return e.getMessage();
        } finally {
            testRepository.save(test);
        }
    }

    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String className, String code) {
            super(URI.create("string:///" + className + ".java"), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    @Override
    public List<Variable> fetchJavaCodeVar(CodeRequest codeRequest) throws IOException {
        String javaCode = codeService.fetchCodeFromGitHub(codeRequest);

        List<Variable> variables = javaCodeAnalyzer.findUserInputVariables(javaCode);

        return variables;
    }

    @Override
    public List<Variable> fetchUserJavaCodeVar(UserCode code) throws IOException {
        List<Variable> variables = javaCodeAnalyzer.findUserInputVariables(code.getCode());
        return variables;
    }

    @Override
    public RunResult runJavaCodeExcel(CodeRequest codeRequest, ArrayList<?> values, int userId) throws IOException {
        String javaScript = codeService.fetchCodeFromGitHub(codeRequest);

        Test test = new Test();
        test.setUrl(codeRequest.getUrlCode());
        test.setTestDate(new Date());
        test.setUser(Math.toIntExact(userId));
        System.out.println(test);

        List<Variable> vars = fetchJavaCodeVar(codeRequest);

        for (int i = 0; i < values.size() && i < vars.size(); i++) {
            Variable var = vars.get(i);
            String varName = var.getName();
            Object varValue = values.get(i);

            String replacement;
            if ("string".equalsIgnoreCase(var.getType())) {
                replacement = varName + " = \"" + varValue.toString() + "\";";
            } else {
                replacement = varName + " = " + varValue.toString() + ";";
            }
            String regex = "\\b" + varName + "\\s*=\\s*(?:.*?(?:\\.nextInt\\(\\)|\\.next\\(\\)|\\.nextLine\\(\\))[^\\n]*)+";
            javaScript = javaScript.replaceAll(regex, replacement);
        }
        System.out.println(javaScript);

        try {
            Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*\\{");
            Matcher matcher = pattern.matcher(javaScript);
            String className;
            if (matcher.find()) {
                className = matcher.group(1);
            } else {
                throw new IllegalArgumentException("Не удалось определить имя класса в коде");
            }

            // Получаем экземпляр Java компилятора
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // Получаем стандартный объект FileManager
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            // Создаем и компилируем исходный файл из строки
            JavaFileObject javaFile = new JavaSourceFromString(className, javaScript);
            Iterable<? extends JavaFileObject> compilationUnits = List.of(javaFile);
            JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, null, null, null, compilationUnits);

            // Проверяем успешность компиляции
            boolean compilationSuccess = compilationTask.call();
            if (!compilationSuccess) {
                // Если компиляция не удалась, возвращаем результат с флагом testResult равным false
                return new RunResult("Compilation failed", false);
            }

            // Закрываем FileManager
            fileManager.close();

            // Создаем загрузчик классов
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(".").toURI().toURL()});

            // Загружаем класс
            Class<?> loadedClass = classLoader.loadClass(className);

            // Пытаемся найти метод main только если компиляция прошла успешно
            Method method = loadedClass.getMethod("main", String[].class);

            // Запускаем метод main и перенаправляем вывод в ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(outputStream));
            method.invoke(null, new Object[]{null});

            // Возвращаем результат выполнения программы
            test.setResult(true);
            return new RunResult(outputStream.toString(StandardCharsets.UTF_8), true);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            // Если возникло исключение при компиляции или выполнении программы, возвращаем результат с флагом testResult равным false
            test.setResult(false);
            return new RunResult(e.getMessage(), false);
        } finally {
            // Сохраняем результат теста в базе данных
            testRepository.save(test);
        }
    }
}
