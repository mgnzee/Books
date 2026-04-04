package ru.vladmz.books.exceptions;

public class GenreNotFoundException extends ResourceNotFoundException {
    public GenreNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreNotFoundException(Integer genreId) {
        super("Genre not found with id: " + genreId);
    }
}
