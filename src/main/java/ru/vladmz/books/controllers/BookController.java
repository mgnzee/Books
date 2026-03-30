package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.security.SecurityUtils;
import ru.vladmz.books.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest bookRequest){
        BookResponse created = service.createBook(BookMapper.toBook(bookRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<BookResponse> selectAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                        @RequestParam(defaultValue = "10", required = false) Integer size,
                                        @RequestParam(defaultValue = "TIME") EntitySort sort,
                                        @RequestParam(defaultValue = "DESC") Sort.Direction direction)
    {
        return service.findAll(page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public BookResponse selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Integer id, @RequestBody @Valid BookPatchRequest book){
        BookResponse updated = service.updateBook(book, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id){
        service.deleteBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}