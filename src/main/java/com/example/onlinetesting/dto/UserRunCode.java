package com.example.onlinetesting.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Data
@Component
public class UserRunCode {
    String code;
    ArrayList<?> values;
}
