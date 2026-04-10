package ru.vladmz.books.DTOs.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.vladmz.books.DTOs.genre.GenreRequest;
import ru.vladmz.books.entities.Genre;

import java.util.Set;

public record BookRequest (
    @NotBlank
    @Size(min = 1, max = 100)
    String title,
    @NotBlank
    String author,
    String description,
    @Size(min = 2, max = 50)
    String language,
    //TODO: NOT FORGET TO UPDATE
//    @NotBlank
//    @URL
    String fileUrl,
    String coverImage,
    Set<GenreRequest> genres
){}
