package ru.vladmz.books.DTOs.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record BookshelfRequest(
    @NotBlank
    @Size(min = 1, max = 100)
    String title,
     String description
) {}
