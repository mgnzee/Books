package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.repositories.BookRepository;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Page<BookResponse> findAll(int page, int size){
        //TODO: SORT BY UPVOTES, AUTHORS, ETC...
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> bookPage = repository.findAll(pageable);
        return bookPage.map(BookResponse::new);
    }

    public BookResponse findById(Integer id){
        return new BookResponse(repository.findById(id).orElseThrow(() -> new BookNotFoundException(id)));
    }

    public BookResponse createBook(Book book){
        return new BookResponse(repository.save(book));
    }

    public BookResponse updateBook(Book book, Integer id){
        Book currentBook = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        if(book.getTitle() != null) currentBook.setTitle(book.getTitle());
        if(book.getAuthor() != null) currentBook.setAuthor(book.getAuthor());
        if(book.getDescription() != null) currentBook.setDescription(book.getDescription());
        if(book.getCoverImage() != null) currentBook.setCoverImage(book.getCoverImage());
        if(book.getLanguage() != null) currentBook.setLanguage(book.getLanguage());
        return new BookResponse(repository.save(currentBook));
    }

    public void deleteBook(Integer id){
        if (!repository.existsById(id)) throw new BookNotFoundException(id);
        repository.deleteById(id);
    }

    //TODO: MOVE TO REPOSITORY
    public void incrementDownloadCount(Integer id){
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        book.setDownloadCount(book.getDownloadCount()+1);
        repository.save(book);
    }
}
