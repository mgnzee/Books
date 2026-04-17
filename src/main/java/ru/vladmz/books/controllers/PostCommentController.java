package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.CommentTarget;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.services.CommentService;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class PostCommentController {

    private final CommentService service;

    @Autowired
    public PostCommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CommentResponse> findByTargetId(@PathVariable Integer postId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "TIME") EntitySort sort,
                                                @RequestParam(defaultValue = "DESC") Sort.Direction direction){
        return service.getCommentsByTargetId(CommentTarget.ofPost(postId), PageParams.of(page, size, sort, direction));
    }

    @GetMapping("/{commentId}")
    public CommentResponse findById(@PathVariable Integer postId, @PathVariable Integer commentId){
        return service.findById(commentId, CommentTarget.ofPost(postId));
    }

    @GetMapping("/{commentId}/replies")
    public Page<CommentResponse> findReplies(@PathVariable Integer postId,
                                             @PathVariable Integer commentId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size){
        return service.findReplies(CommentTarget.ofPost(postId), commentId, PageParams.of(page, size, EntitySort.TIME, Sort.Direction.ASC));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer postId,
                                                         @RequestBody @Valid CommentRequest request){
        CommentResponse created = service.createComment(CommentMapper.patchComment(request), request.parentCommentId(), CommentTarget.ofPost(postId));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Integer postId, @PathVariable Integer commentId,
                                                         @RequestBody @Valid CommentPatchRequest request){
        CommentResponse updated = service.updateComment(request, commentId, CommentTarget.ofPost(postId));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId){
        service.deleteComment(commentId, CommentTarget.ofPost(postId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}