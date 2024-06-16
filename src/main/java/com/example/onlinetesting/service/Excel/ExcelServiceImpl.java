package com.example.onlinetesting.service.Excel;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.RunResult;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.ExcelFile;
import com.example.onlinetesting.repository.ExcelRepository;
import com.example.onlinetesting.service.Analyzer.Variable;
import com.example.onlinetesting.service.Code.Java.JavaService;
import com.example.onlinetesting.service.Code.Python.PythonService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.python.icu.text.SimpleDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final PythonService pythonService;
    private final JavaService javaService;
    private final ExcelRepository excelRepository;

    @Override
    public byte[] createUserExcelFilePython(UserCode userCode) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath = "C:\\MPT\\Дипломка\\" + fileName;
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        List<Variable> variables = pythonService.fetchUserPythonCodeVar(userCode);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Имя");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue("Запишите значения переменной в каждой ячейке строки");
        int rowIndex = 1;
        for (Variable variable : variables) {
            Row row1 = sheet.createRow(rowIndex++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue(variable.getName());
        }
        FileOutputStream fout = new FileOutputStream(filePath);
        workbook.write(fout);
        fout.close();

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelFile = new ExcelFile();
        excelFile.setFileName(fileName);
        excelFile.setData(downloadExcelFromFileSystem(fileName));
        excelRepository.save(excelFile);
        System.out.println("Success");
        return excelFile.getData();
    }

    @Override
    public byte[] createExcelFilePython(CodeRequest codeRequest) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath = "C:\\MPT\\Дипломка\\" + fileName;
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        List<Variable> variables = pythonService.fetchPythonCodeVar(codeRequest);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Имя");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue("Запишите значения переменной в каждой ячейке строки");
        int rowIndex = 1;
        for (Variable variable : variables) {
            Row row1 = sheet.createRow(rowIndex++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue(variable.getName());
        }
        FileOutputStream fout = new FileOutputStream(filePath);
        workbook.write(fout);
        fout.close();

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelFile = new ExcelFile();
        excelFile.setFileName(fileName);
        excelFile.setData(downloadExcelFromFileSystem(fileName));
        excelRepository.save(excelFile);
        System.out.println("Success");
        return excelFile.getData();
    }

    public byte[] downloadExcelFromFileSystem(String fileName) throws IOException {
        String filePath = "C:\\MPT\\Дипломка\\" + fileName;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] uploadPythonFile(MultipartFile file, int userId, CodeRequest codeRequest) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath1 = "C:\\MPT\\Дипломка\\" + fileName;

        Path filePath = Paths.get(file.getOriginalFilename());
        String fileNameWithoutExtension = filePath.getFileName().toString().replaceFirst("[.][^.]+$", "");

        // Формируем новое имя файла с датой и временем загрузки
        String formattedFileName = String.format("%s_%s.xlsx", fileNameWithoutExtension, formattedDateTime);

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelData = ExcelFile.builder()
                .fileName(formattedFileName)
                .data(ExcelUtils.compressExcel(file.getBytes())).build();
        excelData = excelRepository.save(excelData);

        if (excelData != null) {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()));
            List<ArrayList<?>> columnValues = new ArrayList<>();

            try {
                // Получаем первый лист из книги
                Sheet sheet = workbook.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();
                int lastCellNum = sheet.getRow(1).getLastCellNum();

                // Читаем данные по столбцам
                for (int col = 1; col < lastCellNum; col++) {
                    boolean hasData = false; // флаг для проверки наличия данных в столбце
                    ArrayList<Object> columnData = new ArrayList<>();

                    for (int row = 1; row <= lastRowNum; row++) {
                        Row currentRow = sheet.getRow(row);
                        if (currentRow != null) {
                            Cell cell = currentRow.getCell(col);
                            if (cell != null && cell.getCellType() != CellType.BLANK) {
                                hasData = true; // установить флаг, если ячейка не пустая
                                switch (cell.getCellType()) {
                                    case STRING:
                                        columnData.add(cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            Date date = cell.getDateCellValue();
                                            String formattedDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
                                            columnData.add(formattedDate);
                                        } else {
                                            double numericValue = cell.getNumericCellValue();
                                            if (numericValue == Math.floor(numericValue)) {
                                                columnData.add((int) numericValue);
                                            } else {
                                                columnData.add(numericValue);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                columnData.add("");
                            }
                        }
                    }
                    // добавить данные столбца только если он содержит данные
                    if (hasData) {
                        columnValues.add(columnData);
                    }
                }

                // Счетчики успешных и провальных тестов
                int totalTests = 0;
                int passedTests = 0;
                int failedTests = 0;

                // После чтения всех значений столбцов передаем их в метод runPythonCode
                for (ArrayList<?> column : columnValues) {
                    RunResult rr = pythonService.runPythonCodeExcel(codeRequest, column, userId);
                    totalTests++;
                    if (rr.getTestResult()) {
                        passedTests++;
                    } else {
                        failedTests++;
                    }
                    int columnIndex = columnValues.indexOf(column) + 1;
                    for (int rowIndex = 1; rowIndex < column.size(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row == null) {
                            row = sheet.createRow(rowIndex);
                        }
                        Object value = column.get(rowIndex - 1); // исправлено здесь
                        Cell cell = row.createCell(columnIndex);
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        }

                        CellStyle style = workbook.createCellStyle();
                        Cell currentCell = row.getCell(columnIndex);
                        if (!rr.getTestResult()) {
                            style.setFillForegroundColor(IndexedColors.RED.getIndex());
                        } else {
                            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        }
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        currentCell.setCellStyle(style); // Применяем стиль к текущей ячейке
                    }
                }

                // Добавляем общее количество тестов, успешных и провальных в конец файла
                Row summaryRow = sheet.createRow(lastRowNum + 2);
                summaryRow.createCell(0).setCellValue("Total tests:");
                summaryRow.createCell(1).setCellValue(totalTests);

                Row passedRow = sheet.createRow(lastRowNum + 3);
                passedRow.createCell(0).setCellValue("Passed tests:");
                passedRow.createCell(1).setCellValue(passedTests);

                Row failedRow = sheet.createRow(lastRowNum + 4);
                failedRow.createCell(0).setCellValue("Failed tests:");
                failedRow.createCell(1).setCellValue(failedTests);

            } finally {
                FileOutputStream fout = new FileOutputStream(filePath1);
                workbook.write(fout);
                fout.close();
                workbook.close();
            }


            return downloadExcelFromFileSystem(fileName);
        }
        return null;
    }

    @Override
    public byte[] uploadUserPythonFile(MultipartFile file, int userId, UserCode userCode) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath1 = "C:\\MPT\\Дипломка\\" + fileName;

        Path filePath = Paths.get(file.getOriginalFilename());
        String fileNameWithoutExtension = filePath.getFileName().toString().replaceFirst("[.][^.]+$", "");

        // Формируем новое имя файла с датой и временем загрузки
        String formattedFileName = String.format("%s_%s.xlsx", fileNameWithoutExtension, formattedDateTime);

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelData = ExcelFile.builder()
                .fileName(formattedFileName)
                .data(ExcelUtils.compressExcel(file.getBytes())).build();
        excelData = excelRepository.save(excelData);

        if (excelData != null) {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()));
            List<ArrayList<?>> columnValues = new ArrayList<>();

            try {
                // Получаем первый лист из книги
                Sheet sheet = workbook.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();
                int lastCellNum = sheet.getRow(1).getLastCellNum();

                // Читаем данные по столбцам
                for (int col = 1; col < lastCellNum; col++) {
                    boolean hasData = false; // флаг для проверки наличия данных в столбце
                    ArrayList<Object> columnData = new ArrayList<>();

                    for (int row = 1; row <= lastRowNum; row++) {
                        Row currentRow = sheet.getRow(row);
                        if (currentRow != null) {
                            Cell cell = currentRow.getCell(col);
                            if (cell != null && cell.getCellType() != CellType.BLANK) {
                                hasData = true; // установить флаг, если ячейка не пустая
                                switch (cell.getCellType()) {
                                    case STRING:
                                        columnData.add(cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            Date date = cell.getDateCellValue();
                                            String formattedDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
                                            columnData.add(formattedDate);
                                        } else {
                                            double numericValue = cell.getNumericCellValue();
                                            if (numericValue == Math.floor(numericValue)) {
                                                columnData.add((int) numericValue);
                                            } else {
                                                columnData.add(numericValue);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                columnData.add("");
                            }
                        }
                    }
                    // добавить данные столбца только если он содержит данные
                    if (hasData) {
                        columnValues.add(columnData);
                    }
                }

                // Счетчики успешных и провальных тестов
                int totalTests = 0;
                int passedTests = 0;
                int failedTests = 0;

                // После чтения всех значений столбцов передаем их в метод runPythonCode
                for (ArrayList<?> column : columnValues) {
                    RunResult rr = pythonService.runUserPythonCodeExcel(userCode, column, userId);
                    totalTests++;
                    if (rr.getTestResult()) {
                        passedTests++;
                    } else {
                        failedTests++;
                    }
                    int columnIndex = columnValues.indexOf(column) + 1;
                    for (int rowIndex = 1; rowIndex < column.size(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row == null) {
                            row = sheet.createRow(rowIndex);
                        }
                        Object value = column.get(rowIndex - 1); // исправлено здесь
                        Cell cell = row.createCell(columnIndex);
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        }

                        CellStyle style = workbook.createCellStyle();
                        Cell currentCell = row.getCell(columnIndex);
                        if (!rr.getTestResult()) {
                            style.setFillForegroundColor(IndexedColors.RED.getIndex());
                        } else {
                            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        }
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        currentCell.setCellStyle(style); // Применяем стиль к текущей ячейке
                    }
                }

                // Добавляем общее количество тестов, успешных и провальных в конец файла
                Row summaryRow = sheet.createRow(lastRowNum + 2);
                summaryRow.createCell(0).setCellValue("Total tests:");
                summaryRow.createCell(1).setCellValue(totalTests);

                Row passedRow = sheet.createRow(lastRowNum + 3);
                passedRow.createCell(0).setCellValue("Passed tests:");
                passedRow.createCell(1).setCellValue(passedTests);

                Row failedRow = sheet.createRow(lastRowNum + 4);
                failedRow.createCell(0).setCellValue("Failed tests:");
                failedRow.createCell(1).setCellValue(failedTests);

            } finally {
                FileOutputStream fout = new FileOutputStream(filePath1);
                workbook.write(fout);
                fout.close();
                workbook.close();
            }


            return downloadExcelFromFileSystem(fileName);
        }
        return null;
    }

    @Override
    public byte[] createExcelFileJava(CodeRequest codeRequest) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath = "C:\\MPT\\Дипломка\\" + fileName;
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        List<Variable> variables = javaService.fetchJavaCodeVar(codeRequest);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Имя");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue("Запишите значения переменных в ячейках");
        int rowIndex = 1;
        for (Variable variable : variables) {
            Row row1 = sheet.createRow(rowIndex++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue(variable.getName());
        }
        FileOutputStream fout = new FileOutputStream(filePath);
        workbook.write(fout);
        fout.close();

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelFile = new ExcelFile();
        excelFile.setFileName(fileName);
        excelFile.setData(downloadExcelFromFileSystem(fileName));
        excelRepository.save(excelFile);
        System.out.println("Success");
        return excelFile.getData();
    }

    @Override
    public byte[] uploadJavaFile(MultipartFile file, int userId, CodeRequest codeRequest) throws IOException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileName = formattedDateTime + ".xlsx";

        String filePath1 = "C:\\MPT\\Дипломка\\" + fileName;

        Path filePath = Paths.get(file.getOriginalFilename());
        String fileNameWithoutExtension = filePath.getFileName().toString().replaceFirst("[.][^.]+$", "");

        // Формируем новое имя файла с датой и временем загрузки
        String formattedFileName = String.format("%s_%s.xlsx", fileNameWithoutExtension, formattedDateTime);

        // Сохраняем информацию о файле в базу данных
        ExcelFile excelData = ExcelFile.builder()
                .fileName(formattedFileName)
                .data(ExcelUtils.compressExcel(file.getBytes())).build();
        excelData = excelRepository.save(excelData);

        if (excelData != null) {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()));
            List<ArrayList<?>> columnValues = new ArrayList<>();

            try {
                // Получаем первый лист из книги
                Sheet sheet = workbook.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();
                int lastCellNum = sheet.getRow(1).getLastCellNum();

                // Читаем данные по столбцам
                for (int col = 1; col < lastCellNum; col++) {
                    boolean hasData = false; // флаг для проверки наличия данных в столбце
                    ArrayList<Object> columnData = new ArrayList<>();

                    for (int row = 1; row <= lastRowNum; row++) {
                        Row currentRow = sheet.getRow(row);
                        if (currentRow != null) {
                            Cell cell = currentRow.getCell(col);
                            if (cell != null && cell.getCellType() != CellType.BLANK) {
                                hasData = true; // установить флаг, если ячейка не пустая
                                switch (cell.getCellType()) {
                                    case STRING:
                                        columnData.add(cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            Date date = cell.getDateCellValue();
                                            String formattedDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
                                            columnData.add(formattedDate);
                                        } else {
                                            double numericValue = cell.getNumericCellValue();
                                            if (numericValue == Math.floor(numericValue)) {
                                                columnData.add((int) numericValue);
                                            } else {
                                                columnData.add(numericValue);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                columnData.add("");
                            }
                        }
                    }
                    // добавить данные столбца только если он содержит данные
                    if (hasData) {
                        columnValues.add(columnData);
                    }
                }

                // Счетчики успешных и провальных тестов
                int totalTests = 0;
                int passedTests = 0;
                int failedTests = 0;

                // После чтения всех значений столбцов передаем их в метод runPythonCode
                for (ArrayList<?> column : columnValues) {
                    RunResult rr = javaService.runJavaCodeExcel(codeRequest, column, userId);
                    totalTests++;
                    if (rr.getTestResult()) {
                        passedTests++;
                    } else {
                        failedTests++;
                    }
                    int columnIndex = columnValues.indexOf(column) + 1;
                    for (int rowIndex = 1; rowIndex < column.size(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row == null) {
                            row = sheet.createRow(rowIndex);
                        }
                        Object value = column.get(rowIndex - 1); // исправлено здесь
                        Cell cell = row.createCell(columnIndex);
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        }

                        CellStyle style = workbook.createCellStyle();
                        Cell currentCell = row.getCell(columnIndex);
                        if (!rr.getTestResult()) {
                            style.setFillForegroundColor(IndexedColors.RED.getIndex());
                        } else {
                            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
                        }
                        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        currentCell.setCellStyle(style); // Применяем стиль к текущей ячейке
                    }
                }

                // Добавляем общее количество тестов, успешных и провальных в конец файла
                Row summaryRow = sheet.createRow(lastRowNum + 2);
                summaryRow.createCell(0).setCellValue("Total tests:");
                summaryRow.createCell(1).setCellValue(totalTests);

                Row passedRow = sheet.createRow(lastRowNum + 3);
                passedRow.createCell(0).setCellValue("Passed tests:");
                passedRow.createCell(1).setCellValue(passedTests);

                Row failedRow = sheet.createRow(lastRowNum + 4);
                failedRow.createCell(0).setCellValue("Failed tests:");
                failedRow.createCell(1).setCellValue(failedTests);

            } finally {
                FileOutputStream fout = new FileOutputStream(filePath1);
                workbook.write(fout);
                fout.close();
                workbook.close();
            }


            return downloadExcelFromFileSystem(fileName);
        }
        return null;
    }




}
