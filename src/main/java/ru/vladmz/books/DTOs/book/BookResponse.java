package ru.vladmz.books.DTOs.book;

import ru.vladmz.books.entities.Genre;

import java.time.LocalDateTime;
import java.util.Collections;
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
){
    /**
     * For tests only
     * **/
    public static BookResponse testTemplate(Integer id, String title){
        return new BookResponse(
                id,
                title,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                null,
                null,
                null,
                null,
                Collections.emptySet()
        );
    }
    /**
     * For tests only
     * **/
    public static BookResponse testTemplate(Integer id, String title, String fileUrl){
        return new BookResponse(
                id,
                title,
                null,
                null,
                null,
                fileUrl,
                null,
                0,
                0,
                null,
                null,
                null,
                null,
                Collections.emptySet()
        );
    }
}
