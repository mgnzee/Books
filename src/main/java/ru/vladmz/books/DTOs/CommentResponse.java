package ru.vladmz.books.DTOs;

import ru.vladmz.books.entities.Comment;
import java.time.LocalDateTime;

public class CommentResponse {

    private Integer id;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private Integer parentCommentId;
    private String text;
    private Integer upvotes;
    private Integer downvotes;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer repliesAmount;

    public CommentResponse(Comment comment, Integer replyAmount){
        this.id = comment.getId();
        this.text = comment.getText();
        this.upvotes = comment.getUpvotes();
        this.downvotes = comment.getDownvotes();
        this.isDeleted = comment.isDeleted();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.repliesAmount = replyAmount;

        if(comment.getUser() != null){
            this.userId = comment.getUser().getId();
            this.userName = comment.getUser().getName();
            this.userAvatar = comment.getUser().getProfilePicture();
        }

        if (comment.getParentComment() != null) this.parentCommentId = comment.getParentComment().getId();
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
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

    public Boolean isDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getRepliesAmount() {
        return repliesAmount;
    }
}
