package ru.vladmz.books.DTOs.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class BookshelfRequest {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    private String description;

    @URL
    private String cover;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }
}
