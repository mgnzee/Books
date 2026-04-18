package ru.vladmz.books.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.bookshelf.BookshelfPatchRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.etc.pageSorting.DefaultSort;
import ru.vladmz.books.mappers.BookshelfMapper;
import ru.vladmz.books.services.BookshelfService;

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
    public Page<BookshelfResponse> findAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                           @RequestParam(defaultValue = "10", required = false) Integer size,
                                           @RequestParam(defaultValue = "TIME", required = false) DefaultSort sort,
                                           @RequestParam(defaultValue = "DESC", required = false) Sort.Direction direction){
        return service.findAll(PageParams.of(page, size, sort, direction));
    }

    @GetMapping("/{bookshelfId}")
    public BookshelfResponse selectById(@PathVariable Integer bookshelfId){
        return service.findById(bookshelfId);
    }

    @GetMapping("/{bookshelfId}/books")
    public Page<BookResponse> selectBooks(@PathVariable Integer bookshelfId,
                                          @RequestParam(defaultValue = "0", required = false) Integer page,
                                          @RequestParam(defaultValue = "10", required = false) Integer size,
                                          @RequestParam(defaultValue = "TIME", required = false) DefaultSort sort,
                                          @RequestParam(defaultValue = "DESC", required = false) Sort.Direction direction){
        return service.findBooksByBookshelfId(bookshelfId, PageParams.of(page, size, sort, direction));
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
