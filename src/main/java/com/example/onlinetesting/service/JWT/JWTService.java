package com.example.onlinetesting.service.JWT;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JWTService {
    String extractTokenFromHeader(HttpServletRequest request);
    int extractUserId(String token);
    String extractEmail(String token);
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extractClaims, UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);

    int extractUserIdFromToken(String token);
}
