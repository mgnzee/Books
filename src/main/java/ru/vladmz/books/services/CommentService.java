package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.CommentRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.SecurityUtils;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final BookshelfRepository bookshelfRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public CommentService(CommentRepository repository, BookRepository bookRepository, BookshelfRepository bookshelfRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.commentRepository = repository;
        this.bookRepository = bookRepository;
        this.bookshelfRepository = bookshelfRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    private @NonNull Commentable findTarget(@NonNull TargetType targetType, Integer targetId){
        return switch (targetType){
            case BOOK -> bookRepository.findById(targetId).orElseThrow(() -> new BookNotFoundException(targetId));
            case BOOKSHELF -> bookshelfRepository.findById(targetId).orElseThrow(() -> new BookshelfNotFoundException(targetId));
        };
    }

    private void checkPermission(@NonNull Comment comment, Integer commentId){
        if (comment.isDeleted()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment with id: " + commentId + " is already deleted.");
        if (!comment.getUser().getEmail().equals(securityUtils.getCurrentUserEmail()))
            throw new AccessDeniedException("No rights to change comment with id: " + commentId);
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
        String email = securityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(email));
        comment.setUser(user);
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);

        Commentable target = findTarget(targetType, targetId);
        target.incrementCommentCount();
        if (parentCommentId != null) {
            Comment parent = commentRepository.findById(parentCommentId).orElseThrow(() -> new CommentNotFoundException(parentCommentId));
            comment.setParentComment(parent);
            parent.setRepliesCount(parent.getRepliesCount()+1);
        } else comment.setParentComment(null);

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    public CommentResponse updateComment(@NonNull Comment request, Integer commentId, Integer targetId, TargetType targetType){
        findTarget(targetType, targetId);
        Comment comment = commentRepository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() -> new CommentNotFoundException(commentId));
        checkPermission(comment, commentId);
        comment.setText(request.getText());
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    public void deleteComment(Integer commentId, TargetType targetType, Integer targetId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        checkPermission(comment, commentId);
        Commentable target = findTarget(targetType, targetId);
        target.decrementCommentCount();
        if (comment.getParentComment() != null){
            Comment parent = comment.getParentComment();
            parent.setRepliesCount(Math.max(0, parent.getRepliesCount()-1));
            //commentRepository.save(parent);
        }
        comment.delete();
        //commentRepository.save(comment);
    }
}