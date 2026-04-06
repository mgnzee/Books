package ru.vladmz.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.entities.interfaces.Ownable;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post extends BaseEntity implements Commentable, Ownable{

    @Size(max = 150)
    @NotNull
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "text")
    private String text;

    @NotNull
    @Column(name = "upvotes", nullable = false)
    private Integer upvotes = 0;

    @NotNull
    @Column(name = "downvotes", nullable = false)
    private Integer downvotes = 0;

    @NotNull
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    @Override
    public User getOwner() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public void incrementCommentCount() {
        this.commentCount++;
    }

    @Override
    public void decrementCommentCount() {
        this.commentCount--;
    }
}