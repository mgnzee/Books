package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.security.SecurityUtils;
import ru.vladmz.books.services.CommentService;

@RestController
@RequestMapping("/books/{bookId}/comments")
public class BookCommentController {

    private final CommentService service;

    @Autowired
    public BookCommentController(CommentService service, SecurityUtils securityUtils) {
        this.service = service;
    }

    @GetMapping
    public Page<CommentResponse> selectByTargetId(@PathVariable Integer bookId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "TIME") EntitySort sort,
                                                  @RequestParam(defaultValue = "DESC") Sort.Direction direction){
        return service.getCommentsByTargetId(bookId, TargetType.BOOK, page, size, sort, direction);
    }

    @GetMapping("/{commentId}")
    public CommentResponse selectById(@PathVariable Integer bookId, @PathVariable Integer commentId){
        return service.findById(commentId, TargetType.BOOK, bookId);
    }

    @GetMapping("/{commentId}/replies")
    public Page<CommentResponse> selectReplies(@PathVariable Integer bookId,
                                               @PathVariable Integer commentId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size){
        return service.getReplies(bookId, TargetType.BOOK, commentId, page, size);
    }


    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer bookId, @RequestBody @Valid CommentRequest request){
        CommentResponse created = service.saveComment(CommentMapper.toComment(request), request.getParentCommentId(), bookId, TargetType.BOOK);
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
        service.deleteComment(commentId, TargetType.BOOK, bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
