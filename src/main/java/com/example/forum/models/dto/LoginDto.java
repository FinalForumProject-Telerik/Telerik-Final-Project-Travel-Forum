package com.example.forum.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Email;

public class LoginDto {
    @NotEmpty(message = "Email can't be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "Password can't be empty")
    private String password;

    public LoginDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
