package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.CommentNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.CommentRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookshelfRepository bookshelfRepository;
    @Mock
    private PermissionChecker permissionChecker;
    @Mock
    private CurrentUserProvider provider;

    @InjectMocks
    private CommentService commentService;

    Comment comment;
    Integer commendId;
    User owner;

    @BeforeEach
    void setUp(){
        owner = new User(5, "Roma", "roma@mail.ru", "blank");
        comment = new Comment();
        commendId = 1;
        comment.setId(commendId);
        comment.setUser(owner);
        comment.setText("Test comment");
        comment.setTargetId(1);
        comment.setTargetType(TargetType.BOOK);
    }

    @Test
    void findById(){
        when(commentRepository.findByIdAndTarget(commendId, TargetType.BOOK, 1)).thenReturn(Optional.of(comment));
        CommentResponse response = commentService.findById(commendId, TargetType.BOOK, 1);

        assertNotNull(response);
        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        verify(commentRepository, times(commendId)).findByIdAndTarget(commendId, TargetType.BOOK, 1);
    }

    @Test
    void findByIdShouldThrowCommentNotFound(){
        when(commentRepository.findByIdAndTarget(commendId, TargetType.BOOK, 1)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.findById(commendId, TargetType.BOOK, 1));
    }

    @Test
    void findByWithWrongTargetIdShouldThrowCommentNotFound(){
        when(commentRepository.findByIdAndTarget(commendId, TargetType.BOOKSHELF, 1)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.findById(commendId, TargetType.BOOKSHELF, 1));
        verify(commentRepository).findByIdAndTarget(commendId, TargetType.BOOKSHELF, 1);
    }

    @Test
    void saveComment(){
        Comment parent = new Comment();
        parent.setId(5);
        Book book = new Book();
        book.setId(1);
        book.setCommentCount(10);
        when(provider.get()).thenReturn(owner);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.findById(5)).thenReturn(Optional.of(parent));
        commentService.saveComment(comment, parent.getId(), book.getId(), TargetType.BOOK);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment saved = captor.getValue();
        assertEquals(TargetType.BOOK, saved.getTargetType());

        assertEquals(comment.getText(), saved.getText());
        assertEquals(owner, saved.getOwner());
        assertNotNull(saved.getParentComment());
        assertEquals(11, book.getCommentCount());

        verify(commentRepository).save(comment);
    }
}
