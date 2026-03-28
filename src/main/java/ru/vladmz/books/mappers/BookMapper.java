package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.book.BookPatchRequest;
import ru.vladmz.books.DTOs.book.BookRequest;
import ru.vladmz.books.DTOs.book.BookResponse;
import ru.vladmz.books.entities.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(book);
    }

    /**NOTE: After mapping, userId should be added from securityContext
     **/
    public static Book toBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setDescription(request.getDescription());
        book.setLanguage(request.getLanguage());
        book.setFileUrl(request.getFileUrl());
        book.setCoverImage(request.getCoverImage());
        book.setAuthor(request.getAuthor());

        return book;
    }

    public static Book patchBook(Book target, BookPatchRequest request){
        if (request.getTitle() != null) target.setTitle(request.getTitle());
        if (request.getDescription() != null) target.setDescription(request.getDescription());
        if (request.getAuthor() != null) target.setDescription(request.getDescription());
        if (request.getLanguage() != null) target.setLanguage(request.getLanguage());
        if (request.getCoverImage() != null) target.setCoverImage(request.getCoverImage());

        return target;
    }
}

