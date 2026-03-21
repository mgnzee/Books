package ru.vladmz.books.DTOs;

import ru.vladmz.books.entities.Comment;

public interface CommentWithRepliesAmount {

    Comment getComment();

    Integer getReplyAmount();

}
