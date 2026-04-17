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
@RequestMapping("/books/{bookId}/comments")
public class BookCommentController {

    private final CommentService service;

    @Autowired
    public BookCommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CommentResponse> selectByTargetId(@PathVariable Integer bookId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "TIME") EntitySort sort,
                                                  @RequestParam(defaultValue = "DESC") Sort.Direction direction){
        return service.getCommentsByTargetId(CommentTarget.ofBook(bookId), PageParams.of(page, size, sort, direction));
    }

    @GetMapping("/{commentId}")
    public CommentResponse selectById(@PathVariable Integer bookId, @PathVariable Integer commentId){
        return service.findById(commentId, CommentTarget.ofBook(bookId));
    }

    @GetMapping("/{commentId}/replies")
    public Page<CommentResponse> selectReplies(@PathVariable Integer bookId,
                                               @PathVariable Integer commentId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size){
        return service.getReplies(CommentTarget.ofBook(bookId), commentId, PageParams.of(page, size, EntitySort.TIME, Sort.Direction.ASC));
    }


    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer bookId,
                                                         @RequestBody @Valid CommentRequest request){
        CommentResponse created = service.createComment(CommentMapper.patchComment(request), request.getParentCommentId(), CommentTarget.ofBook(bookId));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> changeComment(@PathVariable Integer bookId, @PathVariable Integer commentId,
                                                         @RequestBody @Valid CommentPatchRequest request){
        CommentResponse updated = service.updateComment(request, commentId, CommentTarget.ofBook(bookId));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer bookId, @PathVariable Integer commentId){
        service.deleteComment(commentId, CommentTarget.ofBook(bookId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
