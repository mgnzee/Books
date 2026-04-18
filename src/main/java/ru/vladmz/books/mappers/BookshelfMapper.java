package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.bookshelf.BookshelfPatchRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.entities.Bookshelf;

public class BookshelfMapper {

    private BookshelfMapper(){}

    public static BookshelfResponse toResponse(Bookshelf bookshelf){
        return new BookshelfResponse(bookshelf);
    }

    /**Map BookshelfRequest to Bookshelf
     * NOTE: After mapping, userId should be added from securityContext
     * **/
    public static Bookshelf toBookshelf(BookshelfRequest request){
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setTitle(request.title());
        bookshelf.setDescription(request.description());

        return bookshelf;
    }

    public static void patchBookshelf(Bookshelf target, BookshelfPatchRequest request){
        if (request.title() != null) target.setTitle(request.title());
        if (request.description() != null) target.setDescription(request.description());
    }
}
