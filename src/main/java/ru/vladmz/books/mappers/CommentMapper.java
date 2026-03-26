package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;

public class CommentMapper {

    private CommentMapper(){}

    public static CommentResponse toResponse(Comment comment){
        return new CommentResponse(comment);
    }

    /**Map CommentRequest to Comment
     * NOTE: After mapping, userId should be added from securityContext
     * **/
    public static Comment toComment(CommentRequest request){
        Comment comment = new Comment();
        comment.setTargetType(request.getTargetType());
        comment.setTargetId(request.getTargetId());
        comment.setText(request.getText());

        return comment;
    }

    public static Comment toComment(CommentPatchRequest request){
        Comment comment = new Comment();
        comment.setText(request.getText());
        return comment; //do i really need this?
    }
}
