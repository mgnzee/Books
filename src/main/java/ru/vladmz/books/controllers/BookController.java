package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.BookRequest;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.services.BookService;
import ru.vladmz.books.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    //TODO: REPLACE WITH AUTH
    private final UserService userService;

    @Autowired
    public BookController(BookService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    //TODO: REPLACE WITH AUTH
    private User getCurrentUser(){
        return userService.findById(1);
    }

    @PostMapping
    public BookResponse createBook(@RequestBody @Valid BookRequest bookRequest){
        User currentUser = getCurrentUser();
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setCoverImage(bookRequest.getCoverImage());
        book.setDescription(bookRequest.getDescription());
        book.setLanguage(bookRequest.getLanguage());
        book.setDownloadCount(0);
        book.setUploadedBy(currentUser);
        return service.createBook(book);
    }

    @GetMapping
    public List<BookResponse> selectAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @PatchMapping("/{id}")
    public BookResponse updateBook(@PathVariable Integer id, @RequestBody Book book){
        return service.updateBook(book, id);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Integer id){
        service.deleteBook(id);
    }
}
