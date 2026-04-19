package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfPatchRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.pageSorting.DefaultSort;
import ru.vladmz.books.exceptions.BookAlreadyInBookshelfException;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.BookshelfMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookshelfServiceTest {

    @Mock
    BookshelfRepository bookshelfRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    PermissionChecker permissionChecker;
    @Mock
    CurrentUserProvider provider;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookshelfService bookshelfService;

    Bookshelf bookshelf;
    User user;
    Book book;
    Integer bookshelfId = 1;
    Integer userId = 5;
    Integer bookId = 10;
    String title = "Bookshelf title";

    @BeforeEach
    void setUp(){
        bookshelf = new Bookshelf();
        bookshelf.setId(bookshelfId);
        bookshelf.setTitle(title);
        user = new User();
        user.setId(userId);
        bookshelf.setAuthor(user);
        book = new Book();
        book.setId(bookId);
    }

    @Test
    void findAll(){
        Page<Bookshelf> response = new PageImpl<>(List.of(bookshelf));
        when(bookshelfRepository.findAll(any(Pageable.class))).thenReturn(response);

        Page<BookshelfResponse> result = bookshelfService.findAll(PageParams.of(0, 10, DefaultSort.TIME, Sort.Direction.DESC));

        assertEquals(1, result.getContent().size());
        assertEquals(bookshelf.getId(), result.getContent().getFirst().id());

        verify(bookshelfRepository).findAll(argThat((Pageable p) ->
                p.getPageNumber() == 0 && p.getPageSize() == 10
        ));
    }

    @Test
    void findById(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        BookshelfResponse result = bookshelfService.findById(bookshelfId);

        assertEquals(bookshelf.getId(), result.id());
        assertEquals(bookshelf.getTitle(), result.title());
        verify(bookshelfRepository).findById(bookshelfId);
    }

    @Test
    void findById_shouldThrowBookshelfNotFound(){
        int wrongId = 100;
        when(bookshelfRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.findById(wrongId));
        verify(bookshelfRepository).findById(wrongId);
    }

    @Test
    void createBookshelf(){
        when(provider.get()).thenReturn(user);
        when(bookshelfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookshelfResponse response = bookshelfService.createBookshelf(bookshelf);

        assertNotNull(response);

        ArgumentCaptor<Bookshelf> captor = ArgumentCaptor.forClass(Bookshelf.class);
        verify(bookshelfRepository).save(captor.capture());

        Bookshelf saved = captor.getValue();
        assertEquals(bookshelf.getTitle(), saved.getTitle());
        assertEquals(user, saved.getOwner());
        verify(provider).get();
    }

    @Test
    void generateDefaultBookshelf(){
        when(bookshelfRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bookshelfService.generateDefaultBookshelf(user);

        ArgumentCaptor<Bookshelf> captor = ArgumentCaptor.forClass(Bookshelf.class);
        verify(bookshelfRepository).save(captor.capture());

        Bookshelf saved = captor.getValue();
        assertEquals("My Library", saved.getTitle());
        assertEquals("This is default bookshelf created automatically", saved.getDescription());
        assertEquals(user, saved.getOwner());
    }

    @Test
    void findBooksByBookshelfId(){
        Page<Book> response = new PageImpl<>(List.of(book));
        when(bookshelfRepository.existsById(bookshelfId)).thenReturn(true);
        when(bookRepository.findBooksByBookshelves_Id(eq(bookshelfId), any(Pageable.class))).thenReturn(response);

        Page<BookResponse> result = bookshelfService.findBooksByBookshelfId(bookshelfId,
                PageParams.of(0, 10, DefaultSort.TIME, Sort.Direction.DESC));

        assertEquals(1, result.getContent().size());
        assertEquals(bookId, result.getContent().getFirst().id());
        verify(bookRepository).findBooksByBookshelves_Id(eq(bookshelfId), argThat((Pageable p) ->
                p.getPageNumber() == 0 && p.getPageSize() == 10
        ));
    }

    @Test
    void findBooksByBookshelfId_shouldThrowBookshelfNotFound(){
        int wrongId = 100;
        when(bookshelfRepository.existsById(wrongId)).thenReturn(false);

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.findBooksByBookshelfId(wrongId, PageParams.firstPage()));

        verify(bookshelfRepository).existsById(wrongId);
    }

    @Test
    void addBookToBookshelf(){
        book.setDownloadCount(0);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookResponse result = bookshelfService.addBookToBookshelf(bookshelfId, bookId);

        verify(permissionChecker).checkPermission(bookshelf);
        assertEquals(1, bookshelf.getBooks().size());
        assertEquals(1, book.getDownloadCount());
        assertTrue(bookshelf.getBooks().contains(book));
        assertNotNull(result);
    }

    @Test
    void addBookToBookshelf_shouldThrowBookshelfNotFound(){
        book.setDownloadCount(0);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        assertEquals(0, book.getDownloadCount());
        verify(permissionChecker, never()).checkPermission(any());
        verify(bookRepository, never()).findById(any());
    }

    @Test
    void addBookToBookshelf_shouldThrowBookNotFound(){
        book.setDownloadCount(0);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        assertEquals(0, book.getDownloadCount());
        verify(bookRepository).findById(bookId);
        verify(permissionChecker).checkPermission(bookshelf);
    }

    @Test
    void addBookToBookshelf_shouldThrowAccessDenied(){
        book.setDownloadCount(0);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        doThrow(new AccessDeniedException("Forbidden")).when(permissionChecker).checkPermission(bookshelf);

        assertThrows(AccessDeniedException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        assertEquals(0, book.getDownloadCount());
        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookRepository, never()).findById(bookId);
    }

    @Test
    void addBookToBookshelf_shouldThrowBookAlreadyInBookshelf(){
        bookshelf.addBook(book);
        book.setDownloadCount(1);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(BookAlreadyInBookshelfException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookRepository).findById(bookId);

        assertEquals(1, bookshelf.getBooks().size());
        assertEquals(1, book.getDownloadCount());
    }

    @Test
    void findByUserId(){
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookshelfRepository.findByAuthorId(eq(userId), any())).thenReturn(new PageImpl<>(List.of(bookshelf)));

        Page<BookshelfResponse> response = bookshelfService.findByUserId(userId,
                PageParams.of(0, 10, DefaultSort.TIME, Sort.Direction.DESC));

        assertEquals(1, response.getContent().size());
        assertEquals(bookshelf.getTitle(), response.getContent().getFirst().title());
        verify(bookshelfRepository).findByAuthorId(eq(userId), argThat((Pageable p) ->
                p.getPageNumber() == 0 &&
                p.getPageSize() == 10 &&
                p.getSort().getOrderFor("createdAt") != null &&
                Objects.requireNonNull(p.getSort().getOrderFor("createdAt")).isDescending()
        ));
    }

    @Test
    void findByUserId_shouldThrowUserNotFound(){
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookshelfService.findByUserId(userId, PageParams.firstPage()));

        verify(userRepository).existsById(userId);
        verify(bookshelfRepository, never()).findByAuthorId(any(), any());
    }

    @Test
    void deleteBookFromBookshelf(){
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        bookshelfService.deleteBookFromBookshelf(bookshelfId, bookId);

        verify(bookRepository).existsById(bookId);
        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookshelfRepository).removeBookFromBookshelf(bookshelfId, bookId);
    }

    @Test
    void deleteBookFromBookshelf_shouldThrowBookNotFound(){
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookshelfService.deleteBookFromBookshelf(bookshelfId, bookId));

        verify(bookRepository).existsById(bookId);
        verifyNoInteractions(bookshelfRepository, permissionChecker);
    }

    @Test
    void deleteBookFromBookshelf_shouldThrowBookshelfNotFound(){
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.deleteBookFromBookshelf(bookshelfId, bookId));

        verify(bookRepository).existsById(bookId);
        verify(bookshelfRepository).findById(bookshelfId);
        verifyNoInteractions(permissionChecker);
        verify(bookshelfRepository, never()).removeBookFromBookshelf(bookshelfId, bookId);
    }

    @Test
    void deleteBookFromBookshelf_shouldThrowAccessDenied(){
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        doThrow(AccessDeniedException.class).when(permissionChecker).checkPermission(bookshelf);

        assertThrows(AccessDeniedException.class, () -> bookshelfService.deleteBookFromBookshelf(bookshelfId, bookId));

        verify(bookRepository).existsById(bookId);
        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookshelfRepository, never()).removeBookFromBookshelf(bookshelfId, bookId);
    }

    @Test
    void updateBookshelf(){
        String newTitle = "New bookshelf title";
        String newDesc = "New desc";
        BookshelfPatchRequest request = new BookshelfPatchRequest(newTitle, newDesc);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        BookshelfResponse updated = bookshelfService.updateBookshelf(bookshelfId, request);

        assertEquals(newTitle, updated.title());
        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
    }

    @Test
    void updateBookshelf_shouldThrowBookshelfNotFound(){
        String newTitle = "New bookshelf title";
        String newDesc = "New desc";
        BookshelfPatchRequest request = new BookshelfPatchRequest(newTitle, newDesc);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.updateBookshelf(bookshelfId, request));

        verify(bookshelfRepository).findById(bookshelfId);
        verifyNoInteractions(permissionChecker);
        verify(bookshelfRepository, never()).save(any());
    }

    @Test
    void updateBookshelf_shouldThrowAccessDenied(){
        String newTitle = "New bookshelf title";
        String newDesc = "New desc";
        BookshelfPatchRequest request = new BookshelfPatchRequest(newTitle, newDesc);
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        doThrow(AccessDeniedException.class).when(permissionChecker).checkPermission(bookshelf);

        assertThrows(AccessDeniedException.class, () -> bookshelfService.updateBookshelf(bookshelfId, request));

        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookshelfRepository, never()).save(any());
    }

    @Test
    void deleteBookshelf(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        bookshelfService.deleteBookshelf(bookshelfId);

        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookshelfRepository).delete(bookshelf);
    }

    @Test
    void deleteBookshelf_shouldThrowBookshelfNotFound(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.deleteBookshelf(bookshelfId));

        verify(bookshelfRepository).findById(bookshelfId);
        verifyNoInteractions(permissionChecker);
        verify(bookshelfRepository, never()).delete(any());
    }

    @Test
    void deleteBookshelf_shouldThrowAccessDenied(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        doThrow(AccessDeniedException.class).when(permissionChecker).checkPermission(bookshelf);

        assertThrows(AccessDeniedException.class, () -> bookshelfService.deleteBookshelf(bookshelfId));

        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookshelfRepository, never()).delete(any());
    }
}