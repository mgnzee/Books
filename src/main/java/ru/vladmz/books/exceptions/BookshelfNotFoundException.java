package ru.vladmz.books.exceptions;

public class BookshelfNotFoundException extends ResourceNotFoundException{

    public BookshelfNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookshelfNotFoundException(Integer bookshelfId) {
        super("Bookshelf not found with id: " + bookshelfId);
    }
}
