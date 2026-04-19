package ru.vladmz.books.DTOs.comment;

import ru.vladmz.books.entities.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Integer id,
        Integer userId,
        String userName,
        String userAvatar,
        Integer parentCommentId,
        String text,
        Integer upvotes,
        Integer downvotes,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer repliesAmount
) {
    public CommentResponse withText(String newText) {
        return new CommentResponse(
                id, userId, userName, userAvatar, parentCommentId,
                newText,
                upvotes, downvotes, isDeleted, createdAt, updatedAt, repliesAmount
        );
    }
    public static CommentResponse fromComment(Comment comment){
        String text = comment.isDeleted() ? "This comment was deleted." : comment.getText();
        Integer parentCommentId = comment.getParentComment() == null ? null : comment.getParentComment().getId();

        String username = null;
        String userAvatar = null;
        Integer ownerId = null;

        if (comment.getOwner() != null){
            ownerId = comment.getOwner().getId();
            if (!comment.isDeleted()){
                username = comment.getOwner().getName();
                userAvatar = comment.getOwner().getProfilePicture();
            }
        }

        return new CommentResponse(
                comment.getId(),
                ownerId,
                username,
                userAvatar,
                parentCommentId,
                text,
                comment.getUpvotes(),
                comment.getDownvotes(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getRepliesCount()
        );
    }
}
