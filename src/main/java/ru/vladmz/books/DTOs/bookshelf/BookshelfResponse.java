package ru.vladmz.books.DTOs.bookshelf;

import ru.vladmz.books.entities.Bookshelf;

public record BookshelfResponse(
    Integer id,
    String title,
    String description,
    String cover,
    Integer authorId
) {

    public BookshelfResponse(Bookshelf bookshelf) {
        this(bookshelf.getId(), bookshelf.getTitle(), bookshelf.getDescription(), bookshelf.getCover(), bookshelf.getOwner().getId());
    }
}
