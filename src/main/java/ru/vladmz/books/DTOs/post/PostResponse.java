package ru.vladmz.books.DTOs.post;

import ru.vladmz.books.DTOs.user.UserResponse;

import java.time.LocalDateTime;

public record PostResponse (
        Integer id,
        UserResponse user,
        String title,
        String text,
        Integer upvotes,
        Integer downvotes,
        Integer commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
