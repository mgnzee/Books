package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.FileUploadRequest;
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.genre.GenreRequest;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.Genre;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.etc.StorageDirectory;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.GenreRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService { //TODO: ADD FILE TYPE VALIDATION

    private final BookRepository repository;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider currentUserProvider;
    private final GenreRepository genreRepository;
    private final FileService fileService;

    @Autowired
    public BookService(BookRepository repository, PermissionChecker permissionChecker, CurrentUserProvider currentUserProvider, GenreRepository genreRepository, FileService fileService) {
        this.repository = repository;
        this.permissionChecker = permissionChecker;
        this.currentUserProvider = currentUserProvider;
        this.genreRepository = genreRepository;
        this.fileService = fileService;
    }

    private Book validateBook(Integer bookId){
        Book currentBook = repository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        permissionChecker.checkPermission(currentBook);

        return currentBook;
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> findAll(int page, int size, @NonNull EntitySort sort, Sort.Direction direction){
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.getFieldName()));
        Page<Book> bookPage = repository.findAll(pageable);
        return bookPage.map(BookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Integer bookId){
        return BookMapper.toResponse(repository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId)));
    }

    public BookResponse createBook(Book book, Set<GenreRequest> genres){
        User currentUser = currentUserProvider.get();
        book.setUploadedBy(currentUser);
        addGenresToBook(book, genres);
        Book savedBook = repository.save(book);
        return BookMapper.toResponse(savedBook);
    }

    private void addGenresToBook(Book book, Set<GenreRequest> genres){
        if(genres == null || genres.isEmpty()) return;
        Set<Integer> genreIds = genres.stream().map(GenreRequest::getId).collect(Collectors.toSet());
        List<Genre> foundGenres = genreRepository.findAllById(genreIds);
        foundGenres.forEach(book::addGenre);
    }

    public BookResponse addBookFile(Integer bookId, FileUploadRequest file){
        Book currentBook = validateBook(bookId);

        if (currentBook.getFileUrl() != null) throw new IllegalStateException("Book file already exists and cannot be changed");

        String path = uploadFile(currentBook, file);
        currentBook.setFileUrl(path);

        return BookMapper.toResponse(currentBook);
    }

    private String uploadFile(Book book, FileUploadRequest file){
        return fileService.uploadResource(book.getId(), StorageDirectory.BOOK_FILE, file);
    }

    public BookResponse updateBook(@NonNull BookPatchRequest request, Integer bookId){
        Book currentBook = validateBook(bookId);
        BookMapper.patchBook(currentBook, request);
        if(request.genres() != null){
            currentBook.getGenres().clear();
            if(!request.genres().isEmpty()) addGenresToBook(currentBook, request.genres());
        }
        return BookMapper.toResponse(currentBook);
    }

    public BookResponse updateCover(Integer bookId, FileUploadRequest file){
        Book currentBook = validateBook(bookId);

        String path = uploadPicture(currentBook, file);
        currentBook.setCoverImage(path);

        return BookMapper.toResponse(currentBook);
    }

    private String uploadPicture(Book book, FileUploadRequest file){
        fileService.deleteResource(book.getCoverImage(), StorageDirectory.BOOK_COVER);
        return fileService.uploadResource(book.getId(), StorageDirectory.BOOK_COVER, file);
    }

    public void deletePicture(Integer bookId){
        Book book = validateBook(bookId);

        String currentPicture = book.getCoverImage();
        if (currentPicture == null) return;

        book.setCoverImage(null);
        repository.saveAndFlush(book);

        fileService.deleteResource(currentPicture, StorageDirectory.BOOK_COVER);
    }

    public void deleteBook(Integer bookId){
        Book book = validateBook(bookId);

        if (book.getCoverImage() != null) fileService.deleteResource(book.getCoverImage(), StorageDirectory.BOOK_COVER);
        if (book.getFileUrl() != null) fileService.deleteResource(book.getFileUrl(), StorageDirectory.BOOK_FILE);

        repository.delete(book);
    }
}