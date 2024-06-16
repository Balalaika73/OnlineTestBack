package com.example.onlinetesting.enteties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@Entity
public class VerificationCode {
    @Id // Аннотация для указания поля id как первичного ключа
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String code;
    private Date expirationTime;

    public VerificationCode() {
    }

    public VerificationCode(String email, String code, Date expirationTime) {
        this.email = email;
        this.code = code;
        this.expirationTime = expirationTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
