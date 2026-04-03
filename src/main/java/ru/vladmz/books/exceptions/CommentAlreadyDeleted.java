package ru.vladmz.books.exceptions;

public class CommentAlreadyDeleted extends ResourceAlreadyDeleted {
    public CommentAlreadyDeleted(Integer commentId) {
        super(commentId);
    }
}
