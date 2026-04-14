package ru.vladmz.books.DTOs;

import java.io.InputStream;

public record FileUploadRequest (
        InputStream inputStream,
        String originalFileName,
        String contentType
){}