package ru.vladmz.books.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class UserUpdateRequest {

    @NotBlank(message = "enter the name")
    @Size(min = 1, max = 25, message = "name must be > 1 and < 25")
    private String name;

    @Email(message = "email not valid")
    @NotBlank
    private String email;

    @URL
    private String profilePicture;

    @NotBlank(message = "enter password")
    @Size(min = 8, message = "password must be >= 8 characters")
    private String rawPassword;

}
