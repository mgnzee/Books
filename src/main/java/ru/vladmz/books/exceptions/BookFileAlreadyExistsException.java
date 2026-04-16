package ru.vladmz.books.exceptions;

public class BookFileAlreadyExistsException extends RuntimeException {
    public BookFileAlreadyExistsException(Integer bookId) {
        super("Book with id " + bookId + " already has a file. Create new book instead.");
    }
}
