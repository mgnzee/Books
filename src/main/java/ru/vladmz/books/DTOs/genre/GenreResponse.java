package ru.vladmz.books.DTOs.genre;

import ru.vladmz.books.entities.Genre;

public record GenreResponse(Integer id, String title){
    public GenreResponse(Genre genre){
        this(genre.getId(), genre.getTitle());
    }
}
