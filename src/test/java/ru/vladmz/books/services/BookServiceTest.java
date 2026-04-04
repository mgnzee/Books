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
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CurrentUserProvider provider;

    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private BookService bookService;

    Book book;
    User owner;
    Integer bookId;

    @BeforeEach
    void setUp(){
        owner = new User(5, "Roma", "roma@mail.ru", "blank");
        book = new Book();
        bookId = 1;
        book.setId(bookId);
        book.setUploadedBy(owner);
    }

    @Test
    void findById(){
        book.setTitle("Fight club");
        book.setAuthor("C. Palahniuk");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        BookResponse response =  bookService.findById(bookId);

        assertNotNull(response);
        assertEquals(book.getTitle(), response.title());
        assertEquals(book.getAuthor(), response.author());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void findByIdShouldThrowBookNotFound(){
        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.findById(0));
    }

    @Test
    void findAllShouldPassCorrectPageable(){
        int page = 2;
        int size = 15;
        EntitySort sort = EntitySort.TIME;
        Sort.Direction direction = Sort.Direction.DESC;

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        bookService.findAll(page, size, sort, direction);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(bookRepository).findAll(pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();

        assertAll(() -> assertEquals(page, captured.getPageNumber(), "Page number does not match"),
                () -> assertEquals(size, captured.getPageSize(), "Page size does not match"),
                () -> assertEquals(direction, captured.getSort().getOrderFor(sort.getFieldName()).getDirection(), "Sort direction does not match")
        );
    }

    @Test
    void findAll(){
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<BookResponse> result = bookService.findAll(0, 10, EntitySort.TIME, Sort.Direction.DESC);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getContent().get(0).id());
    }

    @Test
    void findAllShouldReturnEmptyPage(){
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        Page<BookResponse> result = bookService.findAll(0, 10, EntitySort.TIME, Sort.Direction.DESC);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void updateBookShouldThrowAccessDenied(){
        BookPatchRequest request = new BookPatchRequest.Builder().title("New title").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doThrow(new AccessDeniedException("Forbidden"))
                .when(permissionChecker).checkPermission(book);

        assertThrows(AccessDeniedException.class, () -> bookService.updateBook(request, bookId));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBookShouldThrowBookNotFound(){
        BookPatchRequest request = new BookPatchRequest.Builder().title("New title").build();

        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(request, 10));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook(){
        String title = "New title";
        BookPatchRequest request = new BookPatchRequest.Builder().title("New title").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookResponse response = bookService.updateBook(request, bookId);

        assertNotNull(response);
        assertEquals(title, response.title());
        verify(bookRepository, times(1)).findById(bookId);
        verify(permissionChecker, times(1)).checkPermission(book);
    }

    @Test
    void createBook(){
        Book book = new Book("Title", "Author", "Descr", "lan", null, null, 0);

        when(provider.get()).thenReturn(owner);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookResponse response = bookService.createBook(book, Set.of());

        assertNotNull(response);
        assertEquals(book.getTitle(), response.title());
        assertEquals(book.getDescription(), response.description());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());

        Book savedBook = bookCaptor.getValue();
        assertEquals(owner, savedBook.getOwner());
        verify(provider, times(1)).get();
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void deleteBook(){
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.deleteBook(bookId);

        verify(permissionChecker, times(1)).checkPermission(book);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBookShouldThrowAccessDenied(){
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        doThrow(new AccessDeniedException("Forbidden"))
                .when(permissionChecker).checkPermission(book);

        assertThrows(AccessDeniedException.class, () -> bookService.deleteBook(bookId));

        verify(bookRepository, never()).delete(any());
    }

    @Test
    void deleteBookShouldThrowBookNotFound(){
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));
        verify(bookRepository, never()).delete(any());
    }
}
