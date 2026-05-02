package ru.vladmz.books.services;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Genre;
import ru.vladmz.books.exceptions.GenreNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.GenreRepository;

import java.util.List;

@Service
@Transactional
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public GenreService(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Genre> findAll(){
        return genreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Genre findById(Integer genreId){
        return genreRepository.findById(genreId).orElseThrow(() -> new GenreNotFoundException(genreId));
    }

    @Transactional(readOnly = true)
    public Page<Book> findBooksByGenre(Integer genreId, PageParams pageParams){
        if (!genreRepository.existsById(genreId)) throw new GenreNotFoundException(genreId);
        return bookRepository.findAllByGenreId(genreId, pageParams.toPageable());
    }
}
