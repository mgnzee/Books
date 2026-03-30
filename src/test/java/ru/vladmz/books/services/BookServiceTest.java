package ru.vladmz.books.services;


import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import ru.vladmz.books.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private MockedStatic<SecurityUtils> mockedSecurity;

    Book book;
    User owner;
    Integer bookId;

    @BeforeEach
    void setUp(){
        mockedSecurity = mockStatic(SecurityUtils.class);
        owner = new User(5, "Roma", "roma@mail.ru", "blank");
        book = new Book();
        bookId = 1;
        book.setId(bookId);
        book.setUploadedBy(owner);
    }

    @AfterEach
    void cleanUp(){
        mockedSecurity.close();
    }

    @Test
    void findById(){
        book.setTitle("Fight club");
        book.setAuthor("C. Palahniuk");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        BookResponse response =  bookService.findById(bookId);

        assertNotNull(response);
        assertEquals(book.getTitle(), response.getTitle());
        assertEquals(book.getAuthor(), response.getAuthor());
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
        assertEquals(1, result.getContent().get(0).getId());
    }

    @Test
    void findAllShouldReturnEmptyPage(){
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        Page<BookResponse> result = bookService.findAll(0, 10, EntitySort.TIME, Sort.Direction.DESC);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void updateBookShouldThrowAccessDenied(){
        User stranger = new User(3, "NotRoma", "notroma@mail.ru", "blank");
        BookPatchRequest request = new BookPatchRequest("New title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(stranger);

        assertThrows(AccessDeniedException.class, () -> bookService.updateBook(request, bookId));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBookShouldThrowBookNotFound(){
        BookPatchRequest request = new BookPatchRequest("New title");

        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(owner);

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(request, 10));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook(){
        String title = "New title";
        BookPatchRequest request = new BookPatchRequest(title);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(owner);

        BookResponse response = bookService.updateBook(request, bookId);

        assertNotNull(response);
        assertEquals(title, response.getTitle());
        mockedSecurity.verify(SecurityUtils::getCurrentUser, times(1));
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void createBook(){
        Book book = new Book("Title", "Author", "Descr", "lan", null, null, 0);

        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(owner);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookResponse response = bookService.createBook(book);

        assertNotNull(response);
        assertEquals(book.getTitle(), response.getTitle());
        assertEquals(book.getDescription(), response.getDescription());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());

        Book savedBook = bookCaptor.getValue();
        assertEquals(owner, savedBook.getUploadedBy());
    }

    @Test
    void deleteBook(){
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(owner);

        bookService.deleteBook(bookId);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBookShouldThrowAccessDenied(){
        User stranger = new User(3, "NotRoma", "notroma@mail.ru", "blank");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(stranger);

        assertThrows(AccessDeniedException.class, () -> bookService.deleteBook(bookId));
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void deleteBookShouldThrowBookNotFound(){
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(owner);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));
        verify(bookRepository, never()).delete(any());
    }
}
