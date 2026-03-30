package ru.vladmz.books.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfPatchRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.mappers.BookshelfMapper;
import ru.vladmz.books.security.SecurityUtils;
import ru.vladmz.books.services.BookshelfService;

import java.util.List;

@RestController
@RequestMapping("/bookshelves")
public class BookshelfController {

    private final BookshelfService service;

    @Autowired
    public BookshelfController(BookshelfService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookshelfResponse> createBookshelf(@RequestBody BookshelfRequest request){
        BookshelfResponse created = service.createBookshelf(BookshelfMapper.toBookshelf(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<BookshelfResponse> selectAll(){
        return service.findAll();
    }

    @GetMapping("/{bookshelfId}")
    public BookshelfResponse selectById(@PathVariable Integer bookshelfId){
        return service.findById(bookshelfId);
    }

    @GetMapping("/{bookshelfId}/books")
    public List<BookResponse> selectBooks(@PathVariable Integer bookshelfId){
        return service.findBooksByBookshelfId(bookshelfId);
    }

    @PostMapping("/{bookshelfId}/books/{bookId}")
    public ResponseEntity<BookResponse> addBookToBookshelf(@PathVariable Integer bookshelfId, @PathVariable Integer bookId){
        BookResponse created = service.addBookToBookshelf(bookshelfId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{bookshelfId}/books/{bookId}")
    public ResponseEntity<Void> deleteBookFromBookshelf(@PathVariable Integer bookshelfId, @PathVariable Integer bookId){
        service.deleteBookFromBookshelf(bookshelfId, bookId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{bookshelfId}")
    public ResponseEntity<BookshelfResponse> updateBookshelf(@PathVariable Integer bookshelfId, @RequestBody BookshelfPatchRequest bookshelf){
        BookshelfResponse updated = service.updateBookshelf(bookshelfId, bookshelf);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{bookshelfId}")
    public ResponseEntity<Void> deleteBookshelf(@PathVariable Integer bookshelfId){
        service.deleteBookshelf(bookshelfId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
