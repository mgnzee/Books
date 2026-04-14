package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;
import ru.vladmz.books.entities.User;

import java.util.Optional;

public class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        String ownerName = Optional.ofNullable(book.getOwner())
                .map(User::getName)
                .orElse(null);

        Integer ownerId = Optional.ofNullable(book.getOwner())
                .map(User::getId)
                .orElse(null);

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getLanguage(),
                book.getFileUrl(),
                book.getCoverImage(),
                book.getDownloadCount(),
                book.getCommentCount(),
                book.getCreatedAt(),
                book.getUpdatedAt(),
                ownerName,
                ownerId,
                book.getGenres()
        );
    }

    /**NOTE: After mapping, userId should be added from securityContext
     **/
    public static Book toBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setLanguage(request.language());
        book.setFileUrl(request.fileUrl());
        book.setCoverImage(request.coverImage());
        book.setAuthor(request.author());

        return book;
    }

    public static Book patchBook(Book target, BookPatchRequest request){
        if (request.title() != null) target.setTitle(request.title());
        if (request.description() != null) target.setDescription(request.description());
        if (request.author() != null) target.setAuthor(request.author());
        if (request.language() != null) target.setLanguage(request.language());

        return target;
    }
}

