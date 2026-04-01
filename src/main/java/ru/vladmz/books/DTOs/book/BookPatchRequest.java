package ru.vladmz.books.DTOs.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookPatchRequest (
    @Size(min = 1, max = 100)
    String title,
    String author,
    String description,
    @Size(min = 2, max = 50)
    String language,
    //@URL
    String coverImage
){
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        public Builder() {}

        private String title;
        private String author;
        private String description;
        private String language;
        private String coverImage;

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

        public Builder coverImage(String coverImage){
            this.coverImage = coverImage;
            return this;
        }

        public BookPatchRequest build(){
            return new BookPatchRequest(title, author, description, language, coverImage);
        }
    }
}
