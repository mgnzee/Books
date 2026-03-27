package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.security.SecurityUtils;

import java.util.List;

@Service
@Transactional
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookshelfService(BookshelfRepository repository, BookRepository bookRepository) {
        this.bookshelfRepository = repository;
        this.bookRepository = bookRepository;
    }

    private void checkPermission(Bookshelf bookshelf){
        if (!bookshelf.getAuthor().getId().equals(SecurityUtils.getCurrentUser().getId()))
            throw new AccessDeniedException("No rights to change bookshelf with id: " + bookshelf.getId());
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
        return new BookshelfResponse(bookshelfRepository.save(bookshelf));
    }

    //TODO: FIX N+1
    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByBookshelfId(Integer id) {
        Bookshelf bookshelf = bookshelfRepository.findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
        return bookshelf.getBooks().stream().map(BookResponse::new).toList();
    }

    public BookResponse addBookToBookshelf(Integer bookshelfId, Integer bookId){
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        checkPermission(bookshelf);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        bookshelf.getBooks().add(book);
        //bookshelfRepository.save(bookshelf);
        return new BookResponse(book);
    }

    public void deleteBookFromBookshelf(Integer bookshelfId, Integer bookId){
        if (!bookRepository.existsById(bookId)) throw new BookNotFoundException(bookId);
        Bookshelf bookShelf = bookshelfRepository.findById(bookshelfId).orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        checkPermission(bookShelf);
        bookshelfRepository.removeBookFromBookshelf(bookshelfId, bookId);
    }

    public BookshelfResponse updateBookshelf(Integer bookshelfId, Bookshelf bookshelf){
        Bookshelf currentBookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        checkPermission(currentBookshelf);
        if(bookshelf.getTitle() != null) currentBookshelf.setTitle(bookshelf.getTitle());
        if(bookshelf.getDescription() != null) currentBookshelf.setDescription(bookshelf.getDescription());
        if(bookshelf.getCover() != null) currentBookshelf.setCover(bookshelf.getCover());
        return new BookshelfResponse(currentBookshelf);
    }

    public void deleteBookshelf(Integer bookshelfId){
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId).orElseThrow(() -> new BookshelfNotFoundException(bookshelfId));
        checkPermission(bookshelf);
        bookshelfRepository.deleteById(bookshelfId);
    }
}
