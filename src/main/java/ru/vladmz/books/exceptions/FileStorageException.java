package ru.vladmz.books.exceptions;

public class FileStorageException extends RuntimeException {

    public FileStorageException(String message, String bucketName, String filePath) {
        super(String.format("%s [Bucket: %s, Path: %s]", message, bucketName, filePath));
    }

    public FileStorageException(String message, String bucketName, String filePath, Throwable cause) {
        super(String.format("%s [Bucket: %s, Path: %s]", message, bucketName, filePath), cause);
    }
}
