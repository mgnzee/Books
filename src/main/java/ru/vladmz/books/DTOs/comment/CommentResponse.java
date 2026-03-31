package ru.vladmz.books.DTOs.comment;

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

    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.userId = comment.getOwner().getId();
        this.upvotes = comment.getUpvotes();
        this.downvotes = comment.getDownvotes();
        this.isDeleted = comment.isDeleted();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.repliesAmount = comment.getRepliesCount();

        if (comment.isDeleted()){
            this.text = "This comment was deleted.";
            this.userName = null;
            this.userAvatar = null;
        } else{
            this.text = comment.getText();
            if(comment.getOwner() != null){
                this.userName = comment.getOwner().getName();
                this.userAvatar = comment.getOwner().getProfilePicture();
            }
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

    public void setText(String text) {
        this.text = text;
    }
}
