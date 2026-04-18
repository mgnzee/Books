package ru.vladmz.books.exceptions;

public class BookAlreadyInBookshelfException extends RuntimeException {

    public BookAlreadyInBookshelfException(Integer bookId, Integer bookshelfId) {
        super("Book with id %d is already in bookshelf with id %d. Duplicates are not allowed".formatted(bookId, bookshelfId));
    }
}
