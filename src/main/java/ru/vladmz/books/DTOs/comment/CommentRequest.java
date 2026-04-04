package ru.vladmz.books.DTOs.comment;

import jakarta.validation.constraints.NotBlank;
import ru.vladmz.books.etc.TargetType;

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
