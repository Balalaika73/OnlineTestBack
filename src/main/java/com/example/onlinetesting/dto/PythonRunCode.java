package com.example.onlinetesting.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Data
@Component
public class PythonRunCode {
    String url;
    ArrayList<?> values;
}
