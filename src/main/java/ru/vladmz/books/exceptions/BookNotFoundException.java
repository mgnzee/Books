package ru.vladmz.books.exceptions;

public class BookNotFoundException extends ResourceNotFoundException{

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookNotFoundException(Integer bookId) {
        super("Book not found with id: " + bookId);
    }
}
