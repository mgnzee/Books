package ru.vladmz.books.DTOs.book;

import ru.vladmz.books.entities.Genre;

import java.time.LocalDateTime;
import java.util.Set;

public record BookResponse (
    Integer id,
    String title,
    String author,
    String description,
    String language,
    String fileUrl,
    String coverImage,
    Integer downloadCount,
    Integer commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String uploadedByUsername,
    Integer uploadedById,
    Set<Genre> genres
){}
