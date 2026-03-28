package ru.vladmz.books.DTOs.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookPatchRequest {

    @Size(min = 1, max = 100)
    private String title;
    private String author;
    private String description;
    @Size(min = 2, max = 50)
    private String language;
    //    @URL
    private String coverImage;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getCoverImage() {
        return coverImage;
    }

}
