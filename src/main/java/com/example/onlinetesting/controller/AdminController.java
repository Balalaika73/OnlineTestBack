package com.example.onlinetesting.controller;

import com.example.onlinetesting.dto.SignUpAdminRequest;
import com.example.onlinetesting.dto.SignUpRequest;
import com.example.onlinetesting.enteties.Person;
import com.example.onlinetesting.service.Auth.AuthenticationService;
import com.example.onlinetesting.service.Logs.DatabaseLogService;
import com.example.onlinetesting.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {
    private final DatabaseLogService logService;
    private final PersonService personService;
    private final AuthenticationService authenticationService;
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/getLogs")
    public ResponseEntity<List<Map<String, String>>> getLogs() {
        List<Map<String, String>> logs = logService.getAllLogs(); // Метод, который возвращает все логи из базы данных
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<Person>> getAllUsers() {
        List<Person> users = personService.getAllUsers(); // Метод, который возвращает все логи из базы данных
        return ResponseEntity.ok(users);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<Person> sighUp(@RequestBody SignUpAdminRequest signUpRequest) {
        logService.saveLog("INFO", "New user added" + signUpRequest.toString());
        return ResponseEntity.ok(authenticationService.registerUser(signUpRequest));
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        personService.deleteUser(userId);
        logService.saveLog("INFO", "User deleted" + personService.getUserEmailById(userId));
        return ResponseEntity.ok("User with id " + userId + " deleted successfully");
    }

}
