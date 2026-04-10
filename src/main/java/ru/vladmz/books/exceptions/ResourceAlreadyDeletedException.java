package ru.vladmz.books.exceptions;

public class ResourceAlreadyDeletedException extends RuntimeException {

    public ResourceAlreadyDeletedException(Integer resourceId, Throwable cause){
        super("Resource with id: " + resourceId + " is deleted", cause);
    }

    public ResourceAlreadyDeletedException(Integer resourceId) {
        super("Resource with id: " + resourceId + " is deleted");
    }

    public ResourceAlreadyDeletedException(String message) {
        super(message);
    }

}
