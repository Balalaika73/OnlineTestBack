package com.example.onlinetesting.service.JWT;

import com.example.onlinetesting.enteties.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService{
    @Autowired
    private HttpServletRequest request;

    public int extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Integer.parseInt(String.valueOf(claims.get("userId")));
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = this.request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Извлекаем токен, убирая "Bearer "
        }
        return null;
    }

    public String generateToken(UserDetails userDetails){
        Long userId = ((Person) userDetails).getId(); // Получаем идентификатор пользователя из UserDetails
        return Jwts.builder().setSubject(userDetails.getUsername())
                .claim("userId", userId) // Добавляем идентификатор пользователя в токен
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extractClaims, UserDetails userDetails){
        return Jwts.builder().setClaims(extractClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSiginKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSiginKey() {
        byte[] key = Decoders.BASE64.decode("iNMm+O3CBH0RCNz63N/U82jBQ9gJJBYrUN1EPi4Sf4w=");
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpires(token));
    }

    private boolean isTokenExpires(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public int extractUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return Integer.parseInt(String.valueOf(claims.get("userId")));
    }

}
