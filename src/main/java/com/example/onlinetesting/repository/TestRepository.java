package com.example.onlinetesting.repository;

import com.example.onlinetesting.enteties.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    List<Test> findAllByUserAndTestDateAfter(int userId, Date testDate);

    List<Test> findAllByUser(int userId);
}
