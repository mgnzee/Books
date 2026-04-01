package ru.vladmz.books.DTOs.book;

import ru.vladmz.books.entities.Book;

import java.time.LocalDateTime;

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
    Integer uploadedById
){}
