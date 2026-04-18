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
import ru.vladmz.books.DTOs.bookshelf.BookshelfRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.pageSorting.DefaultSort;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.mappers.BookshelfMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.HashSet;
import java.util.List;
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
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookResponse result = bookshelfService.addBookToBookshelf(bookshelfId, bookId);

        verify(permissionChecker).checkPermission(bookshelf);
        assertEquals(1, bookshelf.getBooks().size());
        assertTrue(bookshelf.getBooks().contains(book));
        assertNotNull(result);
    }

    @Test
    void addBookToBookshelf_shouldThrowBookshelfNotFound(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.empty());

        assertThrows(BookshelfNotFoundException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        verify(permissionChecker, never()).checkPermission(any());
        verify(bookRepository, never()).findById(anyInt());
    }

    @Test
    void addBookToBookshelf_shouldThrowBookNotFound(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        verify(bookRepository).findById(bookId);
        verify(permissionChecker).checkPermission(bookshelf);
    }

    @Test
    void addBookToBookshelf_shouldThrowAccessDenied(){
        when(bookshelfRepository.findById(bookshelfId)).thenReturn(Optional.of(bookshelf));

        doThrow(new AccessDeniedException("Forbidden")).when(permissionChecker).checkPermission(bookshelf);

        assertThrows(AccessDeniedException.class, () -> bookshelfService.addBookToBookshelf(bookshelfId, bookId));

        assertTrue(bookshelf.getBooks().isEmpty());
        verify(bookshelfRepository).findById(bookshelfId);
        verify(permissionChecker).checkPermission(bookshelf);
        verify(bookRepository, never()).findById(bookId);
    }

    @Test
    void findByUserId(){

    }

    @Test
    void findByUserId_shouldThrowUserNotFound(){

    }

    @Test
    void deleteBookFromBookshelf(){

    }

    @Test
    void deleteBookFromBookshelf_shouldThrowBookNotFound(){

    }

    @Test
    void deleteBookFromBookshelf_shouldThrowBookshelfNotFound(){

    }

    @Test
    void deleteBookFromBookshelf_shouldThrowAccessDenied(){

    }

    @Test
    void deleteBookFromBookshelf_shouldPassWhenUserIsAdmin(){

    }

    @Test
    void updateBookshelf(){

    }

    @Test
    void updateBookshelf_shouldThrowBookshelfNotFound(){

    }

    @Test
    void updateBookshelf_shouldThrowAccessDenied(){

    }

    @Test
    void updateBookShelf_shouldThrowBookAlreadyInBookshelf(){

    }

    @Test
    void updateBookshelf_shouldPassWhenUserIsAdmin(){

    }

    @Test
    void deleteBookshelf(){

    }

    @Test
    void deleteBookshelf_shouldPassWhenUserIsAdmin(){

    }

    @Test
    void deleteBookshelf_shouldThrowBookshelfNotFound(){

    }

    @Test
    void deleteBookshelf_shouldThrowAccessDenied(){

    }
}
