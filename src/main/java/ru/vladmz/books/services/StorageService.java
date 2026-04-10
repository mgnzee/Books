package ru.vladmz.books.services;

import java.io.InputStream;

public interface StorageService {

    String upload(InputStream inputStream, String path, String contentType);

    void delete(String path);

    boolean exists(String path);

    String getPresignedUrl(String path);

    InputStream download(String path);
}
