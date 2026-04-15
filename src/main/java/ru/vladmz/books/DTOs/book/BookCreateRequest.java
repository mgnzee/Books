package ru.vladmz.books.DTOs.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.vladmz.books.DTOs.genre.GenreRequest;

import java.util.Set;

public record BookCreateRequest(
    @NotBlank
    @Size(min = 1, max = 100)
    String title,
    @NotBlank
    String author,
    String description,
    @Size(min = 2, max = 50)
    String language,
    Set<GenreRequest> genres
){}
