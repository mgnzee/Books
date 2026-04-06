package ru.vladmz.books.exceptions;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostNotFoundException(Integer postId) {
        super("Post not found with id: " + postId);
    }
}
