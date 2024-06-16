package com.example.onlinetesting.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String role;

}
