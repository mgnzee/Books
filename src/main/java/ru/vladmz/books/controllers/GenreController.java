package ru.vladmz.books.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.PageParams;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.DTOs.genre.GenreResponse;
import ru.vladmz.books.etc.pageSorting.BookSort;
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

    @GetMapping("/{genreId}/books")
    public Page<BookResponse> findBooksByGenreId(@PathVariable Integer genreId,
                                                 @RequestParam(defaultValue = "0", required = false) Integer page,
                                                 @RequestParam(defaultValue = "10", required = false) Integer size,
                                                 @RequestParam(defaultValue = "TIME") BookSort sort,
                                                 @RequestParam(defaultValue = "DESC") Sort.Direction direction){
        return genreService.findBooksByGenre(genreId, PageParams.of(page, size, sort, direction)).map(BookMapper::toResponse);
    }
}