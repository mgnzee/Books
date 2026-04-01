package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfPatchRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.mappers.BookshelfMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;

@Service
@Transactional
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final BookRepository bookRepository;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider provider;

    @Autowired
    public BookshelfService(BookshelfRepository repository, BookRepository bookRepository, PermissionChecker permissionChecker, CurrentUserProvider provider) {
        this.bookshelfRepository = repository;
        this.bookRepository = bookRepository;
        this.permissionChecker = permissionChecker;
        this.provider = provider;
    }

    @Transactional(readOnly = true)
    public List<BookshelfResponse> findAll(){
        return bookshelfRepository.findAll().stream().map(BookshelfResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public BookshelfResponse findById(Integer id){
        return new BookshelfResponse(bookshelfRepository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id)));
    }

    public BookshelfResponse createBookshelf(Bookshelf bookshelf) {
        User currentUser = provider.get();
        bookshelf.setAuthor(currentUser);
        return new BookshelfResponse(bookshelfRepository.save(bookshelf));
    }

    //TODO: CHECK FOR N+1
    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByBookshelfId(Integer id) {
        Bookshelf bookshelf = bookshelfRepository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        return bookshelf.getBooks().stream().map(BookMapper::toResponse).toList();
    }

    public BookResponse addBookToBookshelf(Integer bookshelfId, Integer bookId){
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        permissionChecker.checkPermission(bookshelf);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        bookshelf.addBook(book);
        book.incrementDownloadCount();
        return BookMapper.toResponse(book);
    }

    public void deleteBookFromBookshelf(Integer bookshelfId, Integer bookId){
        if (!bookRepository.existsById(bookId)) throw new BookNotFoundException(bookId);
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId).orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        permissionChecker.checkPermission(bookshelf);
        bookshelfRepository.removeBookFromBookshelf(bookshelfId, bookId);
    }

    public BookshelfResponse updateBookshelf(Integer bookshelfId, BookshelfPatchRequest bookshelf){
        Bookshelf currentBookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        permissionChecker.checkPermission(currentBookshelf);
        BookshelfMapper.patchBookshelf(currentBookshelf, bookshelf);
        return BookshelfMapper.toResponse(currentBookshelf);
    }

    public void deleteBookshelf(Integer bookshelfId){
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId).orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        permissionChecker.checkPermission(bookshelf);
        bookshelfRepository.delete(bookshelf);
    }
}
