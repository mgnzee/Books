package ru.vladmz.books.DTOs.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    private String text;

    public PostRequest(){}

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}