package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.repositories.BookRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponse> findAll(){
        return repository.findAll().stream().map(BookResponse::new).toList();
    }

    public BookResponse findById(Integer id){
        return new BookResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Book with id: " + id + " not found")));
    }

    public BookResponse createBook(Book book){
        return new BookResponse(repository.save(book));
    }

    public BookResponse updateBook(Book book, Integer id){
        Book currentBook = repository.findById(id).orElseThrow(() -> new RuntimeException("Book with id: " + id + " not found"));
        if(book.getTitle() != null) currentBook.setTitle(book.getTitle());
        if(book.getAuthor() != null) currentBook.setAuthor(book.getAuthor());
        if(book.getDescription() != null) currentBook.setDescription(book.getDescription());
        if(book.getCoverImage() != null) currentBook.setCoverImage(book.getCoverImage());
        if(book.getLanguage() != null) currentBook.setLanguage(book.getLanguage());
        return new BookResponse(repository.save(currentBook));
    }

    public void deleteBook(Integer id){
        repository.deleteById(id);
    }

    public void incrementDownloadCount(Integer id){
        Book book = repository.findById(id).orElseThrow(() -> new RuntimeException("Book with id: " + id + " not found"));
        book.setDownloadCount(book.getDownloadCount()+1);
        repository.save(book);
    }
}
