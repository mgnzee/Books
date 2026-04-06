package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.*;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.CommentRepository;
import ru.vladmz.books.repositories.PostDao;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;
import ru.vladmz.books.targetStrategies.CommentTargetStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService implements DeletableChecker{

    private final CommentRepository commentRepository;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider provider;
    private final Map<TargetType, CommentTargetStrategy> strategies;

    @Autowired
    public CommentService(CommentRepository repository, PermissionChecker permissionChecker, CurrentUserProvider provider, List<CommentTargetStrategy> strategyList) {
        this.commentRepository = repository;
        this.permissionChecker = permissionChecker;
        this.provider = provider;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(CommentTargetStrategy::getType, strategy -> strategy));
    }

    private @NonNull Commentable findTarget(@NonNull TargetType targetType, Integer targetId){
        CommentTargetStrategy strategy = strategies.get(targetType);
        return strategy.findById(targetId);
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
        targetIncrementCommentCount(targetType, targetId);
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

    private void targetIncrementCommentCount(TargetType targetType, Integer targetId){
        findTarget(targetType, targetId).incrementCommentCount();
    }

    private void targetDecrementCommentCount(TargetType targetType, Integer targetId){
        findTarget(targetType, targetId).decrementCommentCount();
    }


    //TODO: MAKE SURE TARGET ID ACTUALLY MATCHES COMMENT.GETTARGETID
    public CommentResponse updateComment(@NonNull CommentPatchRequest request, Integer commentId, Integer targetId, TargetType targetType){
        findTarget(targetType, targetId);
        Comment comment = commentRepository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        //comment.setText(request.getText());
        CommentMapper.patchComment(comment, request);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    public void deleteComment(Integer commentId, TargetType targetType, Integer targetId){
        Comment comment = commentRepository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        targetDecrementCommentCount(targetType, targetId);
        updateParent(comment);
        comment.delete();
    }

    private void updateParent(Comment comment){
        if (comment.getParentComment() != null){
            Comment parent = comment.getParentComment();
            parent.setRepliesCount(Math.max(0, parent.getRepliesCount()-1));
        }
    }
}