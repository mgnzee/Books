package ru.vladmz.books.DTOs.comment;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank
    private String text;

    private Integer parentCommentId;

    public String getText() {
        return text;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }
}
