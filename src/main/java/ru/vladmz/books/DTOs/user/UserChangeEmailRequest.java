package ru.vladmz.books.DTOs.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserChangeEmailRequest {

    @NotBlank
    @Email
    private String email;

    public UserChangeEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
