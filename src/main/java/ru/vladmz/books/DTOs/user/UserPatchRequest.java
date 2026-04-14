package ru.vladmz.books.DTOs.user;

import jakarta.validation.constraints.Size;

public class UserPatchRequest {

    @Size(min = 1, max = 25, message = "name must be > 1 and < 25")
    private String name;

    public String getName() {
        return name;
    }

}
