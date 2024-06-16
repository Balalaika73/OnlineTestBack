package com.example.onlinetesting.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String token;
    private int userId;
}
