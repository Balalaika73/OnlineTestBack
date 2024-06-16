package com.example.onlinetesting.service;

import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PersonService {
    UserDetailsService userDetailsService();
    public List<Person> getAllUsers();
    public Map<String, Integer> getUsersCountByLast3Months();
    public void deleteUser(int id);
    public String getUserEmailById(int userId);

    int getDaysSinceRegistration(int userId);

    Person getUserById(int userId);

    Profile getProfileById(int userId);
}
