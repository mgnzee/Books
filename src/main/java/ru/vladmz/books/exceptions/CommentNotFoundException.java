package ru.vladmz.books.exceptions;

public class CommentNotFoundException extends ResourceNotFoundException {

    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNotFoundException(Integer commentId) {
        super("Comment not found with id: " + commentId);
    }
}
