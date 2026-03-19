package ru.vladmz.books.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.BookRequest;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.DTOs.CollectionResponse;
import ru.vladmz.books.entities.Collection;
import ru.vladmz.books.services.CollectionService;

import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectionController {

    private final CollectionService service;

    @Autowired
    public CollectionController(CollectionService service) {
        this.service = service;
    }

    @PostMapping()
    public CollectionResponse createCollection(@RequestBody Collection collection){
        return service.createCollection(collection);
    }

    @GetMapping
    public List<CollectionResponse> selectAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CollectionResponse selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookResponse> selectBooks(@PathVariable Integer id){
        return service.findBooksByCollectionId(id);
    }

    //TODO: FINISH THESE METHODS:

    @PostMapping("/{id}/books")
    public BookResponse addBookToCollection(@PathVariable Integer id, @RequestBody BookRequest book){
        return null;
    }

    @DeleteMapping("/{id}/books/{book_id}")
    public void deleteBookFromCollection(@PathVariable Integer id, @PathVariable Integer book_id){

    }

    @PatchMapping("/{id}")
    public CollectionResponse updateCollection(@PathVariable Integer id, @RequestBody Collection collection){
        return service.updateCollection(id, collection);
    }

    @DeleteMapping("/{id}")
    public void deleteCollection(@PathVariable Integer id){
        service.deleteCollection(id);
    }
}
