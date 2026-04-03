package ru.vladmz.books.exceptions;

public class ResourceAlreadyDeleted extends RuntimeException {

    public ResourceAlreadyDeleted(String message, Throwable cause){
        super(message, cause);
    }

    public ResourceAlreadyDeleted(String message) {
        super(message);
    }
}
