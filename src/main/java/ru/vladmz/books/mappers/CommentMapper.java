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
    public static Comment patchComment(CommentRequest request){
        Comment comment = new Comment();
        comment.setText(request.text());

        return comment;
    }


    public static Comment patchComment(Comment target, CommentPatchRequest request){
        if(request.getText()!=null) target.setText(request.getText());
        return target;
    }
}
