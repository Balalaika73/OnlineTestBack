package com.example.onlinetesting.service;

import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Profile;
import com.example.onlinetesting.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
                Person person = personRepository.findByEmail(s);
                if (person == null) {
                    throw new UsernameNotFoundException("User not found");
                }
                return person;
            }

        };
    }

    public List<Person> getAllUsers() {
        return personRepository.findAll();
    }

    @Override
    public Map<String, Integer> getUsersCountByLast3Months() {
        List<Person> users = personRepository.findAll();
        Map<String, Integer> usersCountByMonth = new LinkedHashMap<>();

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
            usersCountByMonth.put(month, 0);
        }

        // Подсчитываем количество пользователей для каждого месяца
        for (Person user : users) {
            Date registerDate = user.getRegisterDate();
            if (registerDate != null) {
                Calendar userCalendar = Calendar.getInstance();
                userCalendar.setTime(registerDate);
                int month = userCalendar.get(Calendar.MONTH);
                String monthName = monthNames[month];
                if (last3Months.contains(monthName)) {
                    usersCountByMonth.put(monthName, usersCountByMonth.get(monthName) + 1);
                }
            }
        }

        return usersCountByMonth;
    }

    @Override
    public void deleteUser(int userId) {
        Optional<Person> userOptional = personRepository.findById(userId);
        if (userOptional.isPresent()) {
            Person user = userOptional.get();
            personRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }
    }
    @Override
    public String getUserEmailById(int userId) {
        Optional<Person> userOptional = personRepository.findById(userId);
        return userOptional.map(Person::getEmail).orElse(null);
    }

    @Override
    public int getDaysSinceRegistration(int userId) {
        Optional<Person> userOptional = personRepository.findById(userId);
        if (userOptional.isPresent()) {
            Person user = userOptional.get();
            Date registrationDate = user.getRegisterDate();
            if (registrationDate != null) {
                Date currentDate = new Date();
                long differenceMillis = currentDate.getTime() - registrationDate.getTime();
                return (int) (differenceMillis / (1000 * 60 * 60 * 24));
            }
        }
        return -1; // Возвращаем -1 в случае ошибки или если дата регистрации пользователя не задана
    }

    @Override
    public Person getUserById(int userId) {
        Optional<Person> userOptional = personRepository.findById(userId);
        return userOptional.orElse(null);
    }

    @Override
    public Profile getProfileById(int userId) {
        Person person = getUserById(userId);
        int days = getDaysSinceRegistration(userId);
        Profile profile = new Profile();
        profile.setPerson(person);
        profile.setUseDays(days);
        return profile;
    }
}