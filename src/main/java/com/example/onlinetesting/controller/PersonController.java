package com.example.onlinetesting.controller;

import com.example.onlinetesting.dto.*;
import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Profile;
import com.example.onlinetesting.service.Auth.AuthenticationService;
import com.example.onlinetesting.service.JWT.JWTService;
import com.example.onlinetesting.service.Logs.DatabaseLogService;
import com.example.onlinetesting.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/person")
@CrossOrigin
@RequiredArgsConstructor
public class PersonController {

    private final AuthenticationService authenticationService;
    private final PersonService personService;
    private final DatabaseLogService databaseLogService;
    private final JWTService jwtService;
    private final HttpServletRequest request;

    @PostMapping("/signUp")
    public ResponseEntity<Person> sighUp(@RequestBody SignUpRequest signUpRequest) {
        databaseLogService.saveLog("INFO", "New user added" + signUpRequest.toString());
        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthenticationResponse> sighIn(@RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(authenticationService.signin(signInRequest));
    }

    @GetMapping("/getProfileInfo")
    public ResponseEntity<Profile> getProfileInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            int userDetails = jwtService.extractUserId(token);

            Profile profile = personService.getProfileById(userDetails);
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/changePass")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String newPassword = changePasswordRequest.getNewPassword();
        try {
            authenticationService.changePassword(email, newPassword);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<Void> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Удаляем "Bearer " из заголовка токена
            Integer userId = jwtService.extractUserIdFromToken(token); // Извлекаем ID пользователя из токена
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Person user = authenticationService.getUserByToken(token);
                if (changeEmailRequest.getNewemail() != null && !changeEmailRequest.getNewemail().isEmpty()) {
                    try {
                        authenticationService.changeEmail(user, changeEmailRequest.getNewemail()); // Вызываем метод для изменения почты
                        return ResponseEntity.ok().build();
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().build(); // Возвращаем ошибку, если что-то пошло не так
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Возвращаем ошибку, если токен некорректный или пользователя не существует
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtAuthenticationResponse refreshedToken = authenticationService.refreshToken(refreshTokenRequest);
        if (refreshedToken != null) {
            // Если токен успешно обновлен, возвращаем его пользователю
            return ResponseEntity.ok(refreshedToken);
        } else {
            // Если обновление токена не удалось, возвращаем ошибку или другой ответ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/getPersonByToken")
    public ResponseEntity<Person> getPersonByToken(@RequestBody String token) {
        return ResponseEntity.ok(authenticationService.getUserByToken(token));
    }

    @PostMapping("/getDaysCount")
    public ResponseEntity<Integer> getDaysCount(@RequestBody String token) {
        int userId = jwtService.extractUserIdFromToken(token); // Извлекаем userId из токена
        int daysCount = personService.getDaysSinceRegistration(userId);
        return ResponseEntity.ok(daysCount);
    }

    @PostMapping("/sendEmailCode")
    public ResponseEntity<String> sendEmailCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String code = authenticationService.sendEmailCode(email);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request) {
        String email = request.getEmail();
        String enteredCode = request.getEnteredCode();
        boolean isValid = authenticationService.verifyCode(email, enteredCode);
        if (isValid) {
            return ResponseEntity.ok("Код подтвержден");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный код");
        }
    }


    @GetMapping("/countThreeMonths")
    public ResponseEntity<Map<String, Integer>> getUsersCountByLast3Months() {
        Map<String, Integer> usersCountByMonth = personService.getUsersCountByLast3Months();
        return ResponseEntity.ok(usersCountByMonth);
    }


}
