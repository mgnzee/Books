package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

@Service
@Transactional
public class BookService {

    private final BookRepository repository;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider currentUserProvider;

    @Autowired
    public BookService(BookRepository repository, PermissionChecker permissionChecker, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.permissionChecker = permissionChecker;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> findAll(int page, int size, @NonNull EntitySort sort, Sort.Direction direction){
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.getFieldName()));
        Page<Book> bookPage = repository.findAll(pageable);
        return bookPage.map(BookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Integer id){
        return BookMapper.toResponse(repository.findById(id).orElseThrow(() -> new BookNotFoundException(id)));
    }

    public BookResponse createBook(Book book){
        User currentUser = currentUserProvider.get();
        book.setUploadedBy(currentUser);
        return BookMapper.toResponse(repository.save(book));
    }

    public BookResponse updateBook(@NonNull BookPatchRequest request, Integer id){
        Book currentBook = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        permissionChecker.checkPermission(currentBook);
        BookMapper.patchBook(currentBook, request);
        return BookMapper.toResponse(currentBook);
    }

    //TODO: ADD ALREADY DELETED CHECK
    public void deleteBook(Integer id){
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        permissionChecker.checkPermission(book);
        repository.delete(book);
    }
}