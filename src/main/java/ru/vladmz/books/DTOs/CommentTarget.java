package ru.vladmz.books.DTOs;

import ru.vladmz.books.etc.TargetType;

public record CommentTarget(
    int id,
    TargetType type
) {

    public static CommentTarget of(int targetId, TargetType targetType){
        return new CommentTarget(targetId, targetType);
    }

    public static CommentTarget ofBook(int bookId){
        return new CommentTarget(bookId, TargetType.BOOK);
    }

    public static CommentTarget ofBookshelf(int bookshelfId){
        return new CommentTarget(bookshelfId, TargetType.BOOKSHELF);
    }

    public static CommentTarget ofPost(int postId){
        return new CommentTarget(postId, TargetType.POST);
    }
}
