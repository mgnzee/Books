package ru.vladmz.books.DTOs.genre;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreRequest {
    private Integer id;

    public GenreRequest() {} // Пустой конструктор обязателен для Jackson

    public GenreRequest(Integer id) {
        this.id = id;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
