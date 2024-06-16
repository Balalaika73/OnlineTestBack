package com.example.onlinetesting.repository;

import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Person findByEmail(String email);

    List<Person> findByRole(Role role);
}
