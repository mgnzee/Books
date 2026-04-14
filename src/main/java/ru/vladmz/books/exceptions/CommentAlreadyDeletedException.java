package ru.vladmz.books.exceptions;

public class CommentAlreadyDeletedException extends ResourceAlreadyDeletedException {
    public CommentAlreadyDeletedException(Integer commentId) {
        super(commentId);
    }
}
