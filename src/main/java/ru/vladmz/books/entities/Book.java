package ru.vladmz.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import jakarta.persistence.Id;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.entities.interfaces.Ownable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book extends BaseEntity implements Commentable, Ownable {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotBlank
    @Size(min = 1, max = 100)
    private String author;

    private String description;

    @NotBlank
    private String language;

    //TODO: ADD RATINGS
    //private Double rating;

    @URL
    //@NotBlank
    @Column(name = "file_url")
    private String fileUrl;

    @URL
    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @ManyToMany(mappedBy = "books")
    private List<Bookshelf> bookshelves = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "books_to_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    public Book(){}

    public Book(String title, String author, String description, String language, String fileUrl, String coverImage, Integer commentCount) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.language = language;
        this.fileUrl = fileUrl;
        this.coverImage = coverImage;
        this.commentCount = commentCount;
        this.downloadCount = 0;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getOwner() {
        return uploadedBy;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public void incrementCommentCount() {
        this.commentCount++;
    }

    @Override
    public void decrementCommentCount() {
        this.commentCount--;
    }

    public void incrementDownloadCount(){
        this.downloadCount++;
    }

    public void decrementDownloadCount(){
        this.downloadCount--;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public List<Bookshelf> getBookshelves() {
        return bookshelves;
    }

    public Set<Genre> getGenres(){
        return genres;
    }

    public Genre addGenre(Genre genre){
        this.genres.add(genre);
//        genre.getBooks().add(this);
        return genre;
    }

    public Genre removeGenre(Genre genre){
        this.genres.remove(genre);
//        genre.getBooks().remove(this);
        return genre;
    }
}