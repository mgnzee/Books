package ru.vladmz.books.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Bookshelf;
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
    public ResponseEntity<BookshelfResponse> createBookshelf(@RequestBody Bookshelf bookshelf){
        BookshelfResponse created = service.createBookshelf(bookshelf);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<BookshelfResponse> selectAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookshelfResponse selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookResponse> selectBooks(@PathVariable Integer id){
        return service.findBooksByBookshelfId(id);
    }

    @PostMapping("/{id}/books/{book_id}")
    public ResponseEntity<BookResponse> addBookToBookshelf(@PathVariable Integer id, @PathVariable Integer book_id){
        BookResponse created = service.addBookToBookshelf(id, book_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}/books/{book_id}")
    public ResponseEntity<Void> deleteBookFromBookshelf(@PathVariable Integer id, @PathVariable Integer book_id){
        service.deleteBookFromBookshelf(id, book_id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookshelfResponse> updateBookshelf(@PathVariable Integer id, @RequestBody Bookshelf bookshelf){
        BookshelfResponse updated = service.updateBookshelf(id, bookshelf);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookshelf(@PathVariable Integer id){
        service.deleteBookshelf(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
