package com.example.onlinetesting.dto;

import com.example.onlinetesting.enteties.Role;
import lombok.Data;

@Data
public class SignUpAdminRequest {

    private String email;
    private Role role;
}