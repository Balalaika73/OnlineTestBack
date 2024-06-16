package com.example.onlinetesting.service.Auth;

import com.example.onlinetesting.dto.*;
import com.example.onlinetesting.enteties.Person;
import org.springframework.stereotype.Service;

public interface AuthenticationService {
    Person signup(SignUpRequest signUpRequest);
    Person registerUser(SignUpAdminRequest SignUpAdminRequest);
    JwtAuthenticationResponse signin(SignInRequest signinRequest);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    Person getUserByToken(String token);

    String sendEmailCode(String email);

    boolean verifyCode(String email, String enteredCode);

    void changePassword(String email, String newPassword);

    void changeEmail(Person user, String newEmail);
}
