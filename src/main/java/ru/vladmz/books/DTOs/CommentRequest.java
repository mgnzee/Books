package ru.vladmz.books.DTOs;

import jakarta.validation.constraints.NotBlank;
import ru.vladmz.books.etc.TargetType;

public class CommentRequest {

    private TargetType targetType;
    private Integer targetId;
    @NotBlank
    private String text;

    private Integer parentCommentId;

    public TargetType getTargetType() {
        return targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public String getText() {
        return text;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }
}
