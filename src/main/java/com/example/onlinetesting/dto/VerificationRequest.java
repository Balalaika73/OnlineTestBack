package com.example.onlinetesting.dto;

public class VerificationRequest {
    private String email;
    private String enteredCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnteredCode() {
        return enteredCode;
    }

    public void setEnteredCode(String enteredCode) {
        this.enteredCode = enteredCode;
    }
}
