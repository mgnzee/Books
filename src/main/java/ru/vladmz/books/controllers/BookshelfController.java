package ru.vladmz.books.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.BookRequest;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.DTOs.BookshelfResponse;
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

    @PostMapping()
    public BookshelfResponse createBookshelf(@RequestBody Bookshelf bookshelf){
        return service.createBookshelf(bookshelf);
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

    //TODO: FINISH THESE METHODS:

    @PostMapping("/{id}/books/{book_id}")
    public BookResponse addBookToBookshelf(@PathVariable Integer id, @PathVariable Integer book_id){
        return service.addBookToBookshelf(id, book_id);
    }

    @DeleteMapping("/{id}/books/{book_id}")
    public void deleteBookFromBookshelf(@PathVariable Integer id, @PathVariable Integer book_id){
        service.deleteBookFromBookshelf(id, book_id);
    }

    @PatchMapping("/{id}")
    public BookshelfResponse updateBookshelf(@PathVariable Integer id, @RequestBody Bookshelf bookshelf){
        return service.updateBookshelf(id, bookshelf);
    }

    @DeleteMapping("/{id}")
    public void deleteBookshelf(@PathVariable Integer id){
        service.deleteBookshelf(id);
    }
}
