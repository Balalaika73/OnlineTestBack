package com.example.onlinetesting.repository;

import com.example.onlinetesting.enteties.ExcelFile;
import com.example.onlinetesting.enteties.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelRepository extends JpaRepository<ExcelFile, Integer> {
}
