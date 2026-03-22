package ru.vladmz.books.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message
){}
