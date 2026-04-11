package ru.vladmz.books.DTOs.user;

import jakarta.validation.constraints.Size;


//TODO: MOVE CHANGING EMAIL SOMEWHERE ELSE
public class UserPatchRequest {

    @Size(min = 1, max = 25, message = "name must be > 1 and < 25")
    private String name;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return null;
    }

}
