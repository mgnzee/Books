package ru.vladmz.books.DTOs.genre;

public class GenreRequest {
    private Integer id;

    public GenreRequest() {}

    public GenreRequest(Integer id) {
        this.id = id;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
