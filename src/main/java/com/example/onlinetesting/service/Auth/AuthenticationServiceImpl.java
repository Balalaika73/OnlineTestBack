package com.example.onlinetesting.service.Auth;

import com.example.onlinetesting.dto.*;
import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.enteties.Role;
import com.example.onlinetesting.enteties.VerificationCode;
import com.example.onlinetesting.repository.PersonRepository;
import com.example.onlinetesting.repository.VerificationCodeRepository;
import com.example.onlinetesting.service.JWT.JWTService;
import com.example.onlinetesting.service.Logs.EmailLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
    private final PersonRepository personRepository;
    private final VerificationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailLogService emailSender;

    @Override
    public Person signup(SignUpRequest signUpRequest) {
        if (!signUpRequest.getRepeatPassword().equals(signUpRequest.getPassword())) {

            System.out.println(signUpRequest.getRepeatPassword());
            throw new IllegalArgumentException("Пароли не совпадают");
        }else {
            Person person = new Person();

            person.setEmail(signUpRequest.getEmail());
            person.setRole(Role.USER);
            person.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            person.setRegisterDate(new Date());

            return personRepository.save(person);
        }
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequest signinRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
        Person user = personRepository.findByEmail(signinRequest.getEmail());
        JwtAuthenticationResponse jwtAuthenticationResponse = null;
        if (user != null) {
            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            jwtAuthenticationResponse.setUserId(user.getId());
            jwtAuthenticationResponse.setRole(user.getRole().toString());
            return jwtAuthenticationResponse;
        } else {
            return null;
        }
    }


    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractEmail(refreshTokenRequest.getToken());
        Person person = personRepository.findByEmail(userEmail);

        if (person != null && jwtService.isTokenValid(refreshTokenRequest.getToken(), person)) {
            var jwt = jwtService.generateToken(person);

            Map<String, Object> refreshClaims = new HashMap<>();
            refreshClaims.put("id", person.getId());
            refreshClaims.put("email", person.getEmail());
            refreshClaims.put("role", person.getRole().toString());

            var refreshToken = jwtService.generateRefreshToken(refreshClaims, person); // Use person to generate a new refresh token

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            jwtAuthenticationResponse.setUserId(person.getId());
            jwtAuthenticationResponse.setRole(person.getRole().toString());
            return jwtAuthenticationResponse;
        }

        return null;
    }



    public Person registerUser(SignUpAdminRequest signUpAdminRequest) {
        Person person = new Person();

        String generatedPassword = generateRandomPassword(6);

        person.setEmail(signUpAdminRequest.getEmail());
        person.setRole(signUpAdminRequest.getRole());
        person.setPassword(passwordEncoder.encode(generatedPassword));
        person.setRegisterDate(new Date());
        try {
            emailSender.sendPass(signUpAdminRequest.getEmail(), "Your New Password", generatedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return personRepository.save(person);
    }

    @Override
    public Person getUserByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        System.out.println(token);
        Integer userId = jwtService.extractUserIdFromToken(token); // Предположим, что у вас есть метод для извлечения ID пользователя из токена
        System.out.println(userId);
        if (userId == null) {
            return null;
        }


        Optional<Person> userOptional = personRepository.findById(userId);
        return userOptional.orElse(null);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            stringBuilder.append(chars.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public static String generateCode() {
        // Создаем объект Random для генерации случайных чисел
        Random random = new Random();
        // Создаем строку для хранения кода
        StringBuilder code = new StringBuilder();

        // Генерируем четыре случайных цифры и добавляем их в строку кода
        for (int i = 0; i < 4; i++) {
            // Генерируем случайную цифру от 0 до 9 и добавляем ее к строке кода
            code.append(random.nextInt(10));
        }

        // Возвращаем сгенерированный четырехзначный код
        return code.toString();
    }

    @Override
    public String sendEmailCode(String email) {
        // Проверяем, существует ли пользователь с указанной почтой
        Person user = personRepository.findByEmail(email);

        if (user != null) {
            // Генерируем код
            String code = generateCode();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            Date expirationTime = calendar.getTime();
            // Создаем объект кода
            VerificationCode verificationCode = new VerificationCode(email, code, expirationTime);
            // Сохраняем код в базе данных
            codeRepository.save(verificationCode);
            // Отправляем код на почту пользователя
            try {
                emailSender.sendCode(email, "Код для сброса пароля", code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code; // Возвращаем сгенерированный код
        } else {
            System.out.println(email);
            throw new NoSuchElementException("Пользователь с почтой " + email + " не найден");
        }
    }


    @Override
    @Transactional
    public boolean verifyCode(String email, String enteredCode) {
        Person user = personRepository.findByEmail(email);
        if (user != null) {
            VerificationCode storedCode = codeRepository.findByEmail(email);
            System.out.println(enteredCode);
            System.out.println(storedCode);
            if (enteredCode != null && storedCode.getCode().equals(enteredCode) && !isCodeExpired(storedCode)) {
                codeRepository.deleteByEmail(email);
                return true;
            } else {
                return false;
            }
        } else {
            throw new NoSuchElementException("Пользователь с почтой " + email + " не найден");
        }
    }

    @Override
    public void changePassword(String email, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Новый пароль не может быть пустым");
        }
        System.out.println(email);
        Person user = personRepository.findByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(user);
    }

    private boolean isCodeExpired(VerificationCode code) {
        Date currentTime = new Date();
        return currentTime.after(code.getExpirationTime());
    }


    @Override
    public void changeEmail(Person user, String newEmail) {
        // Проверяем, существует ли пользователь и новая почта не пустая
        if (user != null && newEmail != null && !newEmail.isEmpty()) {
            // Устанавливаем новую почту пользователю
            user.setEmail(newEmail);
            // Сохраняем изменения в базе данных
            personRepository.save(user);
        } else {
            throw new IllegalArgumentException("Невозможно изменить почту. Пользователь или новая почта не указаны.");
        }
    }

}
