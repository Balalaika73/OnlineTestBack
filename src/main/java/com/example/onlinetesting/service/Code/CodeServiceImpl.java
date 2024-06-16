package com.example.onlinetesting.service.Code;

import com.example.onlinetesting.dto.CodeRequest;
import com.example.onlinetesting.dto.UserCode;
import com.example.onlinetesting.enteties.Test;
import com.example.onlinetesting.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class CodeServiceImpl implements CodeService{

    @Autowired
    private TestRepository testRepository;

    public String fetchCodeFromGitHub(CodeRequest codeRequest) throws IOException {
        String urlCode = codeRequest.getUrlCode();
        URL url = new URL(urlCode);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        reader.close();

        connection.disconnect();
        System.out.println(response.toString());

        return response.toString();
    }

    @Override
    public String fetchUserCode(UserCode userCode) throws IOException {
        String code = userCode.getCode();
        BufferedReader reader = new BufferedReader(new StringReader(code));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        reader.close();
        System.out.println(response.toString());

        return response.toString();
    }

    @Override
    public Map<String, Integer> getTestsCountByLast3Months() {
        List<Test> tests = testRepository.findAll();
        Map<String, Integer> testsCountByMonth = new LinkedHashMap<>();

        // Массив с именами месяцев в именительном падеже на русском языке
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        // Создаем список всех месяцев за последние три месяца в обратном порядке
        List<String> last3Months = new LinkedList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            int month = calendar.get(Calendar.MONTH);
            last3Months.add(0, monthNames[month]); // Добавляем месяц в начало списка
            calendar.add(Calendar.MONTH, -1); // Сдвигаемся на месяц назад
        }

        // Инициализируем счетчики для всех месяцев
        for (String month : last3Months) {
            testsCountByMonth.put(month, 0);
        }

        // Подсчитываем количество пользователей для каждого месяца
        for (Test test : tests) {
            Date testDate = test.getTestDate();
            if (testDate != null) {
                Calendar userCalendar = Calendar.getInstance();
                userCalendar.setTime(testDate);
                int month = userCalendar.get(Calendar.MONTH);
                String monthName = monthNames[month];
                if (last3Months.contains(monthName)) {
                    testsCountByMonth.put(monthName, testsCountByMonth.get(monthName) + 1);
                }
            }
        }

        return testsCountByMonth;
    }

    @Override
    public Map<String, Integer> getUserTestsCountByLast3Months(int userId) {
        // Получаем текущую дату
        Calendar currentCalendar = Calendar.getInstance();
        // Получаем текущий месяц
        int currentMonth = currentCalendar.get(Calendar.MONTH);

        // Отнимаем 2 месяца от текущего месяца
        currentCalendar.add(Calendar.MONTH, -2);
        // Получаем начальную точку для подсчета последних 3 месяцев
        Date threeMonthsAgo = currentCalendar.getTime();

        // Получаем тесты пользователя за последние 3 месяца, включая текущий месяц
        List<Test> tests = testRepository.findAllByUserAndTestDateAfter(userId, threeMonthsAgo);

        Map<String, Integer> testsCountByMonth = new LinkedHashMap<>();

        // Массив с именами месяцев в именительном падеже на русском языке
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        // Создаем список всех месяцев за последние три месяца в обратном порядке
        List<String> last3Months = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            int month = currentCalendar.get(Calendar.MONTH);
            last3Months.add(monthNames[month]); // Добавляем месяц в конец списка
            currentCalendar.add(Calendar.MONTH, 1); // Сдвигаемся на месяц вперед
        }

        // Инициализируем счетчики для всех месяцев
        for (String month : last3Months) {
            testsCountByMonth.put(month, 0);
        }

        // Подсчитываем количество тестов для каждого месяца
        for (Test test : tests) {
            Date testDate = test.getTestDate();
            if (testDate != null) {
                Calendar testCalendar = Calendar.getInstance();
                testCalendar.setTime(testDate);
                int month = testCalendar.get(Calendar.MONTH);
                String monthName = monthNames[month];
                if (last3Months.contains(monthName)) {
                    testsCountByMonth.put(monthName, testsCountByMonth.get(monthName) + 1);
                }
            }
        }

        return testsCountByMonth;
    }

    @Override
    public List<Test> getTestsHistory(int userId) {
        return testRepository.findAllByUser(userId);
    }

}
