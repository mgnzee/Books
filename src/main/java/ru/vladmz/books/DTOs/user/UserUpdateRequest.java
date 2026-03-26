package ru.vladmz.books.DTOs.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


//TODO: MOVE CHANGING EMAIL SOMEWHERE ELSE
public class UserUpdateRequest {

    @Size(min = 1, max = 25, message = "name must be > 1 and < 25")
    private String name;

    @Email(message = "email not valid")
    private String email;

    @URL
    private String profilePicture;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
