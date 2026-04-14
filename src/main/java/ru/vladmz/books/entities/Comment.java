package ru.vladmz.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.vladmz.books.entities.interfaces.Ownable;
import ru.vladmz.books.entities.interfaces.SoftDeletable;
import ru.vladmz.books.etc.TargetType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity implements Ownable, SoftDeletable {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @NotNull
    @Column(name = "target_id")
    private Integer targetId;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replies = new ArrayList<>();

    @NotBlank
    @Size(max = 500)
    private String text;

    private Integer upvotes = 0;
    private Integer downvotes = 0;

    @Column(name = "replies_count", nullable = false)
    private Integer repliesCount = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Comment(){}

    public Comment(TargetType targetType, Integer targetId, Comment parentComment, String text) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.parentComment = parentComment;
        this.text = text;
    }

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){ this.updatedAt = LocalDateTime.now(); }

    @Override
    public User getOwner() {
        return user;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public String getText() {
        return text;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    @Override
    public Boolean isDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void incrementUpvotes() {
        this.upvotes += 1;
    }

    public void decrementUpvotes() {
        this.upvotes -= 1;
    }

    public void incrementDownvotes() {
        this.downvotes += 1;
    }

    public void decrementDownvotes() {
        this.downvotes -= 1;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore(){
        this.isDeleted = false;
    }

    public List<Comment> getReplies(){
        return replies;
    }

    public Integer getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(Integer repliesCount) {
        this.repliesCount = repliesCount;
    }
}
