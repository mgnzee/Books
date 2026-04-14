package ru.vladmz.books.services;

import java.io.InputStream;

public interface StorageService {

    String upload(InputStream inputStream, String bucket, String path, String contentType);

    void delete(String path, String bucket);

    boolean exists(String path, String bucket);

    String getPresignedUrl(String path, String bucket);

    InputStream download(String path, String bucket);
}
