package ru.vladmz.books.DTOs.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest (
    @NotBlank String text,
    Integer parentCommentId
){}