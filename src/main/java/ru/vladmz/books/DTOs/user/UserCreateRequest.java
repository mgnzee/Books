package ru.vladmz.books.DTOs.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class UserCreateRequest {

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

    public UserCreateRequest(String name, String email, String profilePicture, String rawPassword) {
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.rawPassword = rawPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }
}
