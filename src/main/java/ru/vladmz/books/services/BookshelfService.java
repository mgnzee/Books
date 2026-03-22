package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookRequest;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;

import java.util.List;

@Service
public class BookshelfService {

    private final BookshelfRepository repository;
    private final BookRepository bookRepository;

    @Autowired
    public BookshelfService(BookshelfRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    public List<BookshelfResponse> findAll(){
        return repository.findAll().stream().map(BookshelfResponse::new).toList();
    }

    public BookshelfResponse findById(Integer id){
        return new BookshelfResponse(repository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id)));
    }

    public BookshelfResponse createBookshelf(Bookshelf bookshelf) {
        return new BookshelfResponse(repository.save(bookshelf));
    }

    public List<BookResponse> findBooksByBookshelfId(Integer id) {
        Bookshelf bookshelf = repository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        return bookshelf.getBooks().stream().map(BookResponse::new).toList();
    }

    public BookResponse addBookToBookshelf(Integer id, Integer bookId){
        Bookshelf bookshelf = repository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        bookshelf.getBooks().add(book);
        repository.save(bookshelf);
        return new BookResponse(book);
    }

    @Transactional
    public void deleteBookFromBookshelf(Integer id, Integer bookId){
        Bookshelf bookshelf = repository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        Book bookToRemove = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        bookshelf.getBooks().remove(bookToRemove);
        repository.save(bookshelf);
    }

    public BookshelfResponse updateBookshelf(Integer id, Bookshelf bookshelf){
        Bookshelf currentBookshelf = repository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        if(bookshelf.getTitle() != null) currentBookshelf.setTitle(bookshelf.getTitle());
        if(bookshelf.getDescription() != null) currentBookshelf.setDescription(bookshelf.getDescription());
        if(bookshelf.getCover() != null) currentBookshelf.setCover(bookshelf.getCover());
        return new BookshelfResponse(repository.save(currentBookshelf));
    }

    public void deleteBookshelf(Integer id){
        repository.findById(id).orElseThrow(() -> new BookshelfNotFoundException(id));
        repository.deleteById(id);
    }
}
