package ru.vladmz.books.DTOs;

import ru.vladmz.books.entities.Book;

import java.time.LocalDateTime;

public class BookResponse {
    private Integer id;
    private String title;
    private String author;
    private String description;
    private String language;
    private String fileUrl;
    private String coverImage;
    private Integer downloadCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String uploadedByUsername;
    private Integer uploadedById;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.description = book.getDescription();
        this.language = book.getLanguage();
        this.fileUrl = book.getFileUrl();
        this.coverImage = book.getCoverImage();
        this.downloadCount = book.getDownloadCount();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
        this.commentCount = book.getCommentCount();
        this.uploadedById = book.getUploadedBy().getId();
        this.uploadedByUsername = book.getUploadedBy().getName();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUploadedByUsername() {
        return uploadedByUsername;
    }

    public Integer getUploadedById() {
        return uploadedById;
    }

    public Integer getCommentCount() {
        return commentCount;
    }
}
