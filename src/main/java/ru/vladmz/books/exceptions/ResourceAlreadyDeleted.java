package ru.vladmz.books.exceptions;

public class ResourceAlreadyDeleted extends RuntimeException {

    public ResourceAlreadyDeleted(Integer resourceId, Throwable cause){
        super("Resource with id: " + resourceId + " is deleted", cause);
    }

    public ResourceAlreadyDeleted(Integer resourceId) {
        super("Resource with id: " + resourceId + " is deleted");
    }

}
