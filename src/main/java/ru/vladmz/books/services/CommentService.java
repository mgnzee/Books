package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.CommentNotFoundException;
import ru.vladmz.books.mappers.CommentMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.CommentRepository;

import java.util.List;

@Service
@Transactional
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
        bookRepository.findById(targetId)
                .orElseThrow(() -> new BookNotFoundException(targetId));

        return repository.findAllByIdAndTargetId(type, targetId).stream().map(CommentMapper::toResponse).toList();
    }

    public List<CommentResponse> getReplies(Integer targetId, TargetType type, Integer commentId){
        return repository.findReplies(type, targetId, commentId).stream().map(CommentMapper::toResponse).toList();
    }

    public CommentResponse findById(Integer commentId, TargetType targetType, Integer targetId){
        return CommentMapper.toResponse(repository.findByIdAndTarget(commentId, targetType, targetId).orElseThrow(() ->
                new CommentNotFoundException(commentId)));
    }


    //TODO: THIS CERTAINLY SHOULD BE REWRITTEN. I SHOULD MAKE DIFFERENT METHODS FOR DIFFERENT TARGETS OR PROBABLY ADD COMMENTABLE INTERFACE
    @Transactional
    public CommentResponse saveComment(Comment comment, Integer parentCommentId, Integer targetId, TargetType targetType){
        if (parentCommentId != null) {
            Comment parent = repository.findById(parentCommentId).orElseThrow(() -> new CommentNotFoundException(parentCommentId));
            comment.setParentComment(parent);
            parent.setRepliesCount(parent.getRepliesCount()+1);
        } else comment.setParentComment(null);
        switch (targetType){
            case BOOK -> {
                Book book = bookRepository.findById(targetId).orElseThrow(() -> new BookNotFoundException(targetId));
                book.setCommentCount(book.getCommentCount()+1);
                bookRepository.save(book);
            }
            case BOOKSHELF -> {
                //TODO
            }
        }
        Comment savedComment = repository.save(comment);

        return CommentMapper.toResponse(savedComment);
    }

    public CommentResponse updateBookComment(Comment request, Integer commentId, Integer bookId){
        if (!bookRepository.existsById(bookId)) throw new BookNotFoundException(bookId);
        Comment comment = repository.findByIdAndTarget(commentId, TargetType.BOOK, bookId).orElseThrow(() -> new CommentNotFoundException(commentId));
        comment.setText(request.getText());
        return CommentMapper.toResponse(repository.save(comment));
    }

    public void deleteComment(Integer commentId){
        Comment comment = repository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        if (comment.getParentComment() != null){
            Comment parent = comment.getParentComment();
            parent.setRepliesCount(Math.max(0, parent.getRepliesCount()-1));
            repository.save(parent);
        }
        comment.delete();
        repository.save(comment);
    }
}
