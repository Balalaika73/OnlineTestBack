package com.example.onlinetesting.service.Analyzer;

public class Variable {
    private String type;
    private String name;

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
