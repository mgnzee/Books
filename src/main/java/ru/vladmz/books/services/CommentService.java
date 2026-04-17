package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.CommentTarget;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.CommentNotFoundException;
import ru.vladmz.books.exceptions.ResourceNotFoundException;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.repositories.CommentRepository;
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
    public CommentService(CommentRepository repository, PermissionChecker permissionChecker,
                          CurrentUserProvider provider, List<CommentTargetStrategy> strategyList) {
        this.commentRepository = repository;
        this.permissionChecker = permissionChecker;
        this.provider = provider;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(CommentTargetStrategy::getType, strategy -> strategy));
    }

    private @NonNull Commentable findTarget(CommentTarget target){
        CommentTargetStrategy strategy = strategies.get(target.type());
        if (strategy == null) throw new ResourceNotFoundException("Resource not found with type: " + target.type() + " and id: " + target.id());
        return strategy.findById(target.id());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTargetId(CommentTarget target, PageParams page){
        findTarget(target);
        Page<Comment> commentPage = commentRepository.findAllByIdAndTargetId(target.type(), target.id(), page.toPageable());
        return commentPage.map(CommentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> findReplies(CommentTarget target, Integer commentId, PageParams page){
        Page<Comment> commentPage = commentRepository.findReplies(target.type(), target.id(), commentId, page.toPageable());
        return commentPage.map(CommentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CommentResponse findById(Integer commentId, CommentTarget target){
        return CommentMapper.toResponse(commentRepository.findByIdAndTarget(commentId, target.type(), target.id()).orElseThrow(() ->
                new CommentNotFoundException(commentId)));
    }

    public CommentResponse createComment(Comment comment, Integer parentCommentId, CommentTarget target) {
        addPropertiesToComment(comment, target);
        targetIncrementCommentCount(target);

        Optional.ofNullable(parentCommentId)
                .ifPresent(id -> setParent(comment, id));

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    private void addPropertiesToComment(Comment comment, CommentTarget target){
        comment.setUser(provider.get());
        comment.setTargetType(target.type());
        comment.setTargetId(target.id());
    }

    private void setParent(Comment comment, Integer parentCommentId){
        Comment parent = commentRepository.findById(parentCommentId).orElseThrow(() -> new CommentNotFoundException(parentCommentId));
        comment.setParentComment(parent);
        commentRepository.incrementCommentCount(parentCommentId);
    }

    private void targetIncrementCommentCount(CommentTarget target){
        findTarget(target).incrementCommentCount();
    }

    private void targetDecrementCommentCount(CommentTarget target){
        findTarget(target).decrementCommentCount();
    }

    public CommentResponse updateComment(@NonNull CommentPatchRequest request, Integer commentId, CommentTarget target){
        findTarget(target);
        Comment comment = commentRepository.findByIdAndTarget(commentId, target.type(), target.id())
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        CommentMapper.patchComment(comment, request);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    public void deleteComment(Integer commentId, CommentTarget target){
        findTarget(target);
        Comment comment = commentRepository.findByIdAndTarget(commentId, target.type(), target.id())
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        permissionChecker.checkPermission(comment);
        checkDeleted(comment);
        targetDecrementCommentCount(target);
        updateParent(comment);
        comment.delete();
    }

    private void updateParent(Comment comment){
        if (comment.getParentComment() != null){
            Comment parent = comment.getParentComment();
            commentRepository.decrementCommentCount(parent.getId());
        }
    }
}