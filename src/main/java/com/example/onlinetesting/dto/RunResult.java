package com.example.onlinetesting.dto;

import lombok.Data;

@Data
public class RunResult {
    private String output;
    private boolean testResult;

    public RunResult(String output, boolean testResult) {
        this.output = output;
        this.testResult = testResult;
    }
    public String getOutput() {
        return output;
    }

    public boolean getTestResult() {
        return testResult;
    }
}
