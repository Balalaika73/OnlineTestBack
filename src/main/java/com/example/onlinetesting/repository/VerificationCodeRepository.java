package com.example.onlinetesting.repository;

import com.example.onlinetesting.enteties.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    VerificationCode findByEmail(String email);
    void deleteByEmail(String email);
}
