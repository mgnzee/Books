package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vladmz.books.DTOs.FileUploadRequest;
import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookCreateRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.etc.EntitySort;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.services.BookService;

import java.io.IOException;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookCreateRequest bookRequest){
        BookResponse created = service.createBook(BookMapper.toBook(bookRequest), bookRequest.genres());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/file")
    public ResponseEntity<BookResponse> addBookFile(@PathVariable Integer id, @RequestParam MultipartFile file) throws IOException {
        var request = new FileUploadRequest(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        BookResponse updated = service.addBookFile(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public Page<BookResponse> findAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                      @RequestParam(defaultValue = "10", required = false) Integer size,
                                      @RequestParam(defaultValue = "TIME") EntitySort sort,
                                      @RequestParam(defaultValue = "DESC") Sort.Direction direction)
    {
        return service.findAll(page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Integer id){
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Integer id, @RequestBody @Valid BookPatchRequest book){
        BookResponse updated = service.updateBook(book, id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/cover-image")
    public ResponseEntity<BookResponse> updateCover(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        var request = new FileUploadRequest(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        BookResponse updated = service.updateCover(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/cover-image")
    public ResponseEntity<Void> deleteCover(@PathVariable Integer id){
        service.deletePicture(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id){
        service.deleteBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}