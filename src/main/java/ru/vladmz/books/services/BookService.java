package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
public class BookService {

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

    public BookResponse updateBook(@NonNull BookPatchRequest request, Integer id){
        Book currentBook = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        permissionChecker.checkPermission(currentBook);
        BookMapper.patchBook(currentBook, request);
        if(request.genres() != null){
            currentBook.getGenres().clear();
            if(!request.genres().isEmpty()) addGenresToBook(currentBook, request.genres());
        }
        return BookMapper.toResponse(currentBook);
    }

    public BookResponse updateCover(Integer bookId, MultipartFile file){
        Book currentBook = repository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        permissionChecker.checkPermission(currentBook);

        String path = uploadPicture(currentBook, file);
        currentBook.setCoverImage(path);

        return BookMapper.toResponse(currentBook);
    }

    private String uploadPicture(Book book, MultipartFile file){
        fileService.deletePicture(book.getCoverImage(), StorageDirectory.BOOK_COVER);
        return fileService.uploadPicture(book.getId(), StorageDirectory.BOOK_COVER, file);
    }

    public void deleteBook(Integer id){
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        permissionChecker.checkPermission(book);
        repository.delete(book);
    }
}