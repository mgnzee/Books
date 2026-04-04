package ru.vladmz.books.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.genre.GenreResponse;
import ru.vladmz.books.mappers.BookMapper;
import ru.vladmz.books.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreResponse> findAll(){
        return genreService.findAll().stream().map(GenreResponse::new).toList();
    }

    @GetMapping("/{genreId}")
    public GenreResponse findById(@PathVariable Integer genreId){
        return new GenreResponse(genreService.findById(genreId));
    }

    //TODO: ADD PAGINATION
    @GetMapping("/{genreId}/books")
    public List<BookResponse> findBooksByGenreId(@PathVariable Integer genreId){
        return genreService.findBooksByGenre(genreId).stream().map(BookMapper::toResponse).toList();
    }
}
