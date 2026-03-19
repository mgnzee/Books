package ru.vladmz.books.DTOs;

import ru.vladmz.books.entities.Collection;

public class CollectionResponse {
    private Integer id;
    private String title;
    private String description;
    private String cover;
    private Integer authorId;

    public CollectionResponse(Collection collection){
        this.id = collection.getId();
        this.title = collection.getTitle();
        this.description = collection.getDescription();
        this.cover = collection.getCover();
        this.authorId = collection.getAuthor().getId();
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
