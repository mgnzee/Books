package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
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
                book.getOwner().getName(),
                book.getOwner().getId(),
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
        if (request.coverImage() != null) target.setCoverImage(request.coverImage());

        return target;
    }
}

