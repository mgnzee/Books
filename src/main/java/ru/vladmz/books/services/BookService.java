package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.security.SecurityUtils;

@Service
@Transactional
public class BookService {

    private final BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    private void checkPermission(Book book){
        if (!book.getUploadedBy().getId().equals(SecurityUtils.getCurrentUser().getId()))
            throw new AccessDeniedException("No rights to change book with id: " + book.getId());
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> findAll(int page, int size, @NonNull EntitySort sort, Sort.Direction direction){
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.getFieldName()));
        Page<Book> bookPage = repository.findAll(pageable);
        return bookPage.map(BookResponse::new);
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Integer id){
        return new BookResponse(repository.findById(id).orElseThrow(() -> new BookNotFoundException(id)));
    }

    public BookResponse createBook(Book book){
        User currentUser = SecurityUtils.getCurrentUser();
        book.setUploadedBy(currentUser);
        return new BookResponse(repository.save(book));
    }

    public BookResponse updateBook(@NonNull BookPatchRequest request, Integer id){
        Book currentBook = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        checkPermission(currentBook);
        BookMapper.patchBook(currentBook, request);
        return new BookResponse(currentBook);
    }

    public void deleteBook(Integer id){
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        checkPermission(book);
        repository.delete(book);
    }
}