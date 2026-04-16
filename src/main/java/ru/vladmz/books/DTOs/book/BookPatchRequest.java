package ru.vladmz.books.DTOs.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.vladmz.books.DTOs.genre.GenreRequest;

import java.util.HashSet;
import java.util.Set;

public record BookPatchRequest (
    @NotBlank
    @Size(min = 1, max = 100)
    String title,
    String author,
    String description,
    @Size(min = 2, max = 50)
    String language,
    Set<GenreRequest> genres
){
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private Builder() {}

        private String title;
        private String author;
        private String description;
        private String language;
        private final Set<GenreRequest> genres = new HashSet<>();

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder author(String author){
            this.author = author;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder language(String language){
            this.language = language;
            return this;
        }

        public Builder genres(Set<GenreRequest> genres){
            this.genres.addAll(genres);
            return this;
        }

        public Builder genre(GenreRequest genre){
            this.genres.add(genre);
            return this;
        }

        public BookPatchRequest build(){
            return new BookPatchRequest(title, author, description, language, genres);
        }
    }
}
