package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.Commentable;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.exceptions.CommentNotFoundException;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.CommentRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.Optional;

@Service
@Transactional
public class CommentService{

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final BookshelfRepository bookshelfRepository;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider provider;

    @Autowired
    public CommentService(CommentRepository repository, BookRepository bookRepository, BookshelfRepository bookshelfRepository, PermissionChecker permissionChecker, CurrentUserProvider provider) {
        this.commentRepository = repository;
        this.bookRepository = bookRepository;
        this.bookshelfRepository = bookshelfRepository;
        this.permissionChecker = permissionChecker;
        this.provider = provider;
    }

    private @NonNull Commentable findTarget(@NonNull TargetType targetType, Integer targetId){
        return switch (targetType){
            case BOOK -> bookRepository.findById(targetId).orElseThrow(() -> new BookNotFoundException(targetId));
            case BOOKSHELF -> bookshelfRepository.findById(targetId).orElseThrow(() -> new BookshelfNotFoundException(targetId));
        };
    }

    //TODO: MOVE HTTP DETAILS AWAY FROM SERVICE BUSINESS LOGIC
    private void checkDeleted(@NonNull Comment comment){
        if (comment.isDeleted()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment with id: " + comment.getId() + " is already deleted.");
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTargetId(Integer targetId, TargetType targetType, int page, int size, @NonNull EntitySort sortBy, Sort.Direction direction){
        findTarget(targetType, targetId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy.getFieldName()));
        Page<Comment> commentPage = commentRepository.findAllByIdAndTargetId(targetType, targetId, pageable);
        return commentPage.map(CommentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getReplies(Integer targetId, TargetType type, Integer commentId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> commentPage = commentRepository.findReplies(type, targetId, commentId, pageable);
        return commentPage.map(CommentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CommentResponse findById(Integer commentId, TargetType targetType, Integer targetId){
        return CommentMapper.toResponse(commentRepository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() ->
                new CommentNotFoundException(commentId)));
    }

    public CommentResponse saveComment(Comment comment, Integer parentCommentId, Integer targetId, TargetType targetType){
        addPropertiesToComment(comment, targetType, targetId);
        updateTarget(targetType, targetId);
        if (parentCommentId != null) setParent(comment, parentCommentId);
        else comment.setParentComment(null);

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    private void addPropertiesToComment(Comment comment, TargetType targetType, Integer targetId){
        comment.setUser(provider.get());
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);
    }

    private void setParent(Comment comment, Integer parentCommentId){
        Comment parent = commentRepository.findById(parentCommentId).orElseThrow(() -> new CommentNotFoundException(parentCommentId));
        comment.setParentComment(parent);
        parent.setRepliesCount(Optional.ofNullable(parent.getRepliesCount()).orElse(0) + 1);
    }

    private void updateTarget(TargetType targetType, Integer targetId){
        Commentable target = findTarget(targetType, targetId);
        target.incrementCommentCount();
    }

    public CommentResponse updateComment(@NonNull Comment request, Integer commentId, Integer targetId, TargetType targetType){
        findTarget(targetType, targetId);
        Comment comment = commentRepository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        comment.setText(request.getText());
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    public void deleteComment(Integer commentId, TargetType targetType, Integer targetId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        Commentable target = findTarget(targetType, targetId);
        target.decrementCommentCount();
        if (comment.getParentComment() != null){
            Comment parent = comment.getParentComment();
            parent.setRepliesCount(Math.max(0, parent.getRepliesCount()-1));
        }
        comment.delete();
    }
}