package com.example.onlinetesting.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
    private String repeatPassword;
}
