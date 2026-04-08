package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.vladmz.books.DTOs.comment.CommentPatchRequest;
import ru.vladmz.books.DTOs.comment.CommentResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.*;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.CommentRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;
import ru.vladmz.books.targetStrategies.CommentTargetStrategy;

import java.util.List;
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
    private PermissionChecker permissionChecker;
    @Mock
    private CurrentUserProvider provider;
    @Mock
    private CommentTargetStrategy postStrategy;
    @Mock
    private CommentTargetStrategy bookStrategy;
    @Mock
    private CommentTargetStrategy bookshelfStrategy;

    private CommentService commentService;

    private Comment comment;
    private Integer commentId;
    private User owner;
    private Book book;

    @BeforeEach
    void setUp(){
        owner = new User(5, "Roma", "roma@mail.ru", "blank");
        comment = new Comment();
        commentId = 1;
        comment.setId(commentId);
        comment.setUser(owner);
        comment.setText("Test comment");
        comment.setTargetId(1);
        comment.setTargetType(TargetType.BOOK);
        book = new Book();
        book.setId(1);
        book.setCommentCount(10);

        when(postStrategy.getType()).thenReturn(TargetType.POST);
        when(bookStrategy.getType()).thenReturn(TargetType.BOOK);
        when(bookshelfStrategy.getType()).thenReturn(TargetType.BOOKSHELF);
        commentService = new CommentService(commentRepository, permissionChecker, provider, List.of(postStrategy, bookStrategy, bookshelfStrategy));
    }

    @Test
    void findById(){
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.of(comment));
        CommentResponse response = commentService.findById(commentId, TargetType.BOOK, 1);

        assertNotNull(response);
        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        verify(commentRepository, times(commentId)).findByIdAndTarget(commentId, TargetType.BOOK, 1);
    }

    @Test
    void findByIdShouldThrowCommentNotFound(){
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.findById(commentId, TargetType.BOOK, 1));
    }

    @Test
    void findByWithWrongTargetIdShouldThrowCommentNotFound(){
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOKSHELF, 1)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.findById(commentId, TargetType.BOOKSHELF, 1));
        verify(commentRepository).findByIdAndTarget(commentId, TargetType.BOOKSHELF, 1);
    }

    @Test
    void saveComment(){
        Comment parent = new Comment();
        parent.setId(5);
        when(provider.get()).thenReturn(owner);
        when(bookStrategy.findById(1)).thenReturn(book);
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

    //TODO: write other save comment tests

    @Test
    void updateComment(){
        CommentPatchRequest request = new CommentPatchRequest();
        request.setText("Updated text");
        when(bookStrategy.findById(1)).thenReturn(book);
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.ofNullable(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        commentService.updateComment(request, commentId, 1, TargetType.BOOK);

        assertEquals("Updated text", comment.getText());
        verify(permissionChecker, times(1)).checkPermission(comment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void updateComment_shouldThrowCommentNotFound(){
        CommentPatchRequest request = new CommentPatchRequest();
        request.setText("Updated text");
        when(bookStrategy.findById(1)).thenReturn(book);
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(request, commentId, 1, TargetType.BOOK));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_shouldThrowTargetNotFound(){
        CommentPatchRequest request = new CommentPatchRequest();
        request.setText("Updated text");
        when(bookStrategy.findById(1)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(request, commentId, 1, TargetType.BOOK));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_shouldThrowAccessDenied(){
        CommentPatchRequest request = new CommentPatchRequest();
        request.setText("Updated text");
        when(bookStrategy.findById(1)).thenReturn(book);
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.ofNullable(comment));

        doThrow(new AccessDeniedException("Forbidden")).when(permissionChecker).checkPermission(comment);

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(request, commentId, 1, TargetType.BOOK));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_shouldThrowCommentAlreadyDeleted(){
        comment.delete();
        CommentPatchRequest request = new CommentPatchRequest();
        request.setText("Updated text");
        when(bookStrategy.findById(1)).thenReturn(book);
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.ofNullable(comment));

        assertThrows(ResourceAlreadyDeletedException.class, () -> commentService.updateComment(request, commentId, 1, TargetType.BOOK));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment(){
        when(bookStrategy.findById(1)).thenReturn(book);
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 1)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId, TargetType.BOOK, 1);

        verify(permissionChecker, times(1)).checkPermission(comment);
        assertTrue(comment.isDeleted());
    }

    @Test
    void deleteComment_shouldThrowCommentNotFound(){
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 10)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId, TargetType.BOOK, 10));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrowTargetNotFound() {
        doThrow(new ResourceNotFoundException("")).when(commentRepository).findByIdAndTarget(commentId, TargetType.BOOK, 150);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(commentId, TargetType.BOOK, 150));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrowAccessDenied(){
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 10)).thenReturn(Optional.of(comment));
        doThrow(new AccessDeniedException("")).when(permissionChecker).checkPermission(comment);

        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(commentId, TargetType.BOOK, 10));

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrowResourceAlreadyDeleted(){
        comment.delete();
        when(commentRepository.findByIdAndTarget(commentId, TargetType.BOOK, 10)).thenReturn(Optional.of(comment));


        assertThrows(ResourceAlreadyDeletedException.class, () -> commentService.deleteComment(commentId, TargetType.BOOK, 10));
        verify(permissionChecker, times(1)).checkPermission(comment);
        verify(commentRepository, never()).delete(any());
    }
}
