package com.example.onlinetesting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResultSummary {
    private int positiveResults;
    private int negativeResults;
}
