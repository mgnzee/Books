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
import ru.vladmz.books.etc.pageSorting.CommentSort;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.services.CommentService;

@RestController
@RequestMapping("/bookshelves/{bookshelfId}/comments")
public class BookshelfCommentController {

    private final CommentService service;

    @Autowired
    public BookshelfCommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CommentResponse> findByTargetId(@PathVariable Integer bookshelfId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "TIME") CommentSort sort,
                                                @RequestParam(defaultValue = "DESC") Sort.Direction direction){
        return service.getCommentsByTargetId(CommentTarget.ofBookshelf(bookshelfId), PageParams.of(page, size, sort, direction));
    }

    @GetMapping("/{commentId}")
    public CommentResponse findById(@PathVariable Integer bookshelfId, @PathVariable Integer commentId){
        return service.findById(commentId, CommentTarget.ofBookshelf(bookshelfId));
    }

    @GetMapping("/{commentId}/replies")
    public Page<CommentResponse> findReplies(@PathVariable Integer bookshelfId,
                                             @PathVariable Integer commentId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size){
        return service.findReplies(CommentTarget.ofBookshelf(bookshelfId), commentId, PageParams.of(page, size, CommentSort.TIME, Sort.Direction.ASC));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Integer bookshelfId,
                                                         @RequestBody @Valid CommentRequest request){
        CommentResponse created = service.createComment(CommentMapper.patchComment(request), request.parentCommentId(), CommentTarget.ofBookshelf(bookshelfId));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Integer bookshelfId, @PathVariable Integer commentId,
                                                         @RequestBody @Valid CommentPatchRequest request){
        CommentResponse updated = service.updateComment(request, commentId, CommentTarget.ofBookshelf(bookshelfId));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer bookshelfId, @PathVariable Integer commentId){
        service.deleteComment(commentId, CommentTarget.ofBookshelf(bookshelfId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
