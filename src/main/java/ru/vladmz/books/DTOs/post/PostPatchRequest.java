package ru.vladmz.books.DTOs.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostPatchRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    private String text;

    public PostPatchRequest(){}

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    /**
     * Only for tests
     * **/
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Only for tests
     * **/
    public void setText(String text) {
        this.text = text;
    }
}
