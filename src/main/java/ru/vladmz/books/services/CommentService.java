package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.CommentResponse;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.CommentNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repository;
    private final BookRepository bookRepository;

    @Autowired
    public CommentService(CommentRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }


    //TODO: FIX N+1 PROBLEM HERE:
    public List<CommentResponse> getCommentsByTargetId(Integer targetId, TargetType type){
//        bookRepository.findById(targetId)
//                .orElseThrow(() -> new BookNotFoundException(targetId));

        return repository.findCommentsWithRepliesAmount(type, targetId).stream()
                .map(result -> {
                    Comment comment = (Comment) result[0];
                    Long repliesAmount = (Long) result[1];
                    return new CommentResponse(comment, repliesAmount);
                }).toList();
    }

    public List<CommentResponse> getReplies(Integer targetId, TargetType type, Integer commentId){
        return repository.findReplies(type, targetId, commentId).stream()
                .map(result -> {
                    Comment comment = (Comment) result[0];
                    Long repliesAmount = (Long) result[1];
                    return new CommentResponse(comment, repliesAmount);
                }).toList();
    }

    public CommentResponse findById(Integer commentId, TargetType targetType, Integer targetId){
        return repository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() ->
                new CommentNotFoundException(commentId));
    }


    @Transactional
    public CommentResponse saveComment(Comment comment, Integer parentCommentId){
        if (parentCommentId != null) {
            Comment parent = repository.findById(parentCommentId).orElseThrow(() -> new CommentNotFoundException(parentCommentId));
            comment.setParentComment(parent);
        } else comment.setParentComment(null);
        Comment savedComment = repository.save(comment);
        return new CommentResponse(savedComment, 0L);
    }
}
