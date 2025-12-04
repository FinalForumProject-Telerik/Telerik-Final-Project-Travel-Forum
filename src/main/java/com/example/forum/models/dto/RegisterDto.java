package com.example.forum.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterDto extends LoginDto {

    @NotEmpty(message = "Password confirmation can't be empty")
    private String passwordConfirm;

    @NotEmpty(message = "First name can't be empty")
    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 symbols")
    private String firstName;

    @NotEmpty(message = "Last name can't be empty")
    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 symbols")
    private String lastName;

    @NotEmpty(message = "Username can't be empty")
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 symbols")
    private String username;

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
