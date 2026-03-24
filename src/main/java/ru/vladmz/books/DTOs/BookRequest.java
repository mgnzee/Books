package ru.vladmz.books.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class BookRequest {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
    @NotBlank
    private String author;
    private String description;
    private String language;
    //TODO: NOT FORGET TO UPDATE
//    @NotBlank
//    @URL
    private String fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public String getCoverImage() {
        return coverImage;
    }

}
