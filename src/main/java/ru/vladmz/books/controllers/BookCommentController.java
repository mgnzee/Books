package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.security.SecurityUtils;
import ru.vladmz.books.services.CommentService;

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
        Comment comment = CommentMapper.toComment(request);
        User user = SecurityUtils.getCurrentUser();
        comment.setUser(user);
        comment.setTargetType(TargetType.BOOK);
        comment.setTargetId(bookId);
        CommentResponse created = service.saveComment(comment, request.getParentCommentId(), bookId, TargetType.BOOK);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> changeComment(@PathVariable Integer bookId, @PathVariable Integer commentId, @RequestBody @Valid CommentPatchRequest request){
        Comment comment = CommentMapper.toComment(request);
        CommentResponse updated = service.updateComment(comment, commentId, bookId, TargetType.BOOK);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer bookId, @PathVariable Integer commentId){
        service.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
