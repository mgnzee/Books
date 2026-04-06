package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.post.PostPatchRequest;
import ru.vladmz.books.DTOs.post.PostRequest;
import ru.vladmz.books.DTOs.post.PostResponse;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.entities.Post;

public class PostMapper {

    private PostMapper(){}

    public static Post toPost(PostRequest request){
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        return post;
    }

    /**Map PostRequest to Comment
     * NOTE: After mapping, userId should be added from securityContext
     * **/
    public static Post patchPost(Post target, PostPatchRequest request){
        if(request.getTitle()!=null) target.setTitle(request.getTitle());
        if(request.getText()!=null) target.setText(request.getText());
        return target;
    }

    public static PostResponse toResponse(Post post, UserResponse owner){
        return new PostResponse(
                post.getId(),
                owner,
                post.getTitle(),
                post.getText(),
                post.getUpvotes(),
                post.getDownvotes(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

}
