package ru.vladmz.books.DTOs.bookshelf;

import ru.vladmz.books.entities.Bookshelf;

public class BookshelfResponse {
    private Integer id;
    private String title;
    private String description;
    private String cover;
    private Integer authorId;

    public BookshelfResponse(Bookshelf bookshelf){
        this.id = bookshelf.getId();
        this.title = bookshelf.getTitle();
        this.description = bookshelf.getDescription();
        this.cover = bookshelf.getCover();
        this.authorId = bookshelf.getOwner().getId();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }

    public Integer getAuthorId() {
        return authorId;
    }
}
