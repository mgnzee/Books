package ru.vladmz.books.DTOs.comment;

import jakarta.validation.constraints.NotBlank;

public class CommentPatchRequest {

    @NotBlank
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
