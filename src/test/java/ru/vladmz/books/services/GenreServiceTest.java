package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Genre;
import ru.vladmz.books.exceptions.GenreNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GenreService genreService;

    Genre genre;

    @BeforeEach
    void setUp(){
        genre = new Genre(1, "Classic");
    }

    @Test
    void findAll(){
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        List<Genre> result = genreService.findAll();

        assertEquals(1, result.size());
        assertEquals(List.of(genre), result);
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList(){
        when(genreRepository.findAll()).thenReturn(List.of());

        List<Genre> result = genreService.findAll();

        assertTrue(result.isEmpty());
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void findById(){
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        Genre result = genreService.findById(1);

        assertEquals(genre, result);
        verify(genreRepository, times(1)).findById(1);
    }

    @Test
    void findById_shouldThrowGenreNotFoundException(){
        when(genreRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findById(5));
    }

    @Test
    void findBooksByGenre(){
        Book book = new Book();
        book.setId(100);
        book.setTitle("La Divina Commedia");
        book.getGenres().add(genre);
        when(bookRepository.findAllByGenreId(1)).thenReturn(List.of(book));
        when(genreRepository.existsById(1)).thenReturn(true);

        List<Book> result = genreService.findBooksByGenre(1);

        assertEquals(1, result.size());
        assertEquals(book, result.get(0));
        verify(bookRepository, times(1)).findAllByGenreId(1);
    }

    @Test
    void findBooksByGenre_shouldThrowGenreNotFoundException(){
        when(genreRepository.existsById(15)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> genreService.findBooksByGenre(15));
        verify(bookRepository, never()).findAllByGenreId(any());
    }
}
