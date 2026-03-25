package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.CommentRequest;
import ru.vladmz.books.DTOs.CommentResponse;
import ru.vladmz.books.DTOs.UserResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.security.SecurityUtils;
import ru.vladmz.books.services.CommentService;
import ru.vladmz.books.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/books/{bookId}/comments")
public class BookCommentController {

    private final CommentService service;

    @Autowired
    public BookCommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommentResponse> selectByTargetId(@PathVariable Integer bookId){
        return service.getCommentsByTargetId(bookId, TargetType.BOOK);
    }

    @GetMapping("/{commentId}")
    public CommentResponse selectById(@PathVariable Integer bookId, @PathVariable Integer commentId){
        return service.findById(commentId, TargetType.BOOK, bookId);
    }

    @GetMapping("/{commentId}/replies")
    public List<CommentResponse> selectReplies(@PathVariable Integer bookId, @PathVariable Integer commentId){
        return service.getReplies(bookId, TargetType.BOOK, commentId);
    }


    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer bookId, @RequestBody @Valid CommentRequest request){
        Comment comment = new Comment();
        User user = SecurityUtils.getCurrentUser();
        comment.setText(request.getText());
        comment.setTargetType(TargetType.BOOK);
        comment.setTargetId(bookId);
        comment.setUser(user);
        CommentResponse created = service.saveComment(comment, request.getParentCommentId(), bookId, TargetType.BOOK);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //TODO: IMPLEMENT CRUD METHODS FOR COMMENTS

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> changeComment(@PathVariable Integer bookId, @PathVariable Integer commentId, @RequestBody @Valid CommentRequest request){
        return null;
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer bookId, @PathVariable Integer commentId){
        return null;
    }
}
