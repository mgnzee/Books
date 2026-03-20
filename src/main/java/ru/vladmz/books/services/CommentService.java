package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.CommentRequest;
import ru.vladmz.books.DTOs.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repository;

    @Autowired
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }


    //TODO: FIX N+1 PROBLEM HERE:
    public List<CommentResponse> getCommentsByTargetId(Integer targetId, TargetType type){
        return repository.findByTargetTypeAndTargetIdAndParentCommentIsNull(type, targetId).stream()
                .map(c -> new CommentResponse(c, repository.getRepliesCount(c.getId()))).toList();
    }

    public List<CommentResponse> getAllReplies(Integer targetId, TargetType type, Integer commentId){
        return repository.findByTargetTypeAndTargetIdAndParentCommentId(type, targetId, commentId).stream()
                .map(c -> new CommentResponse(c, repository.getRepliesCount(c.getId()))).toList();
    }

    public CommentResponse findById(Integer commentId, TargetType targetType, Integer targetId){
        return new CommentResponse(repository.findByIdAndTarget(commentId, targetType, targetId)
                .orElseThrow(() -> new RuntimeException("Comment with id: " + commentId + " not found")),
                repository.getRepliesCount(commentId));
    }


    public CommentResponse saveComment(Comment comment){
        Comment savedComment = repository.save(comment);
        return new CommentResponse(savedComment, repository.getRepliesCount(savedComment.getId()));
    }
}
