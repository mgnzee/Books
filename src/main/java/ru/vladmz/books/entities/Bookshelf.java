package ru.vladmz.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import ru.vladmz.books.services.Ownable;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "bookshelves")
public class Bookshelf implements Commentable, Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    private String title;

    private String description;

    @URL
    private String cover;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "bookshelves_to_books",
            joinColumns = @JoinColumn(name = "bookshelf_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    public Bookshelf() {}

    public Bookshelf(String title, String description, String cover) {
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){ this.updatedAt = LocalDateTime.now(); }

    @Override
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public User getOwner() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public Book addBook(Book book){
        this.books.add(book);
        book.getBookshelves().add(this);
        return book;
    }

    public Book removeBook(Book book){
        this.books.remove(book);
        book.getBookshelves().remove(this);
        return book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookshelf that = (Bookshelf) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public void incrementCommentCount() {
        //TODO: ADD COMMENTS TO BOOKSHELVES
    }

    @Override
    public void decrementCommentCount() {
        //TODO: ADD COMMENTS TO BOOKSHELVES
    }
}
