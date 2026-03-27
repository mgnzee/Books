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
        bookshelf.setTitle(request.getTitle());
        bookshelf.setDescription(request.getDescription());
        bookshelf.setCover(request.getCover());

        return bookshelf;
    }

    public static void patchBookshelf(Bookshelf target, BookshelfPatchRequest request){
        if (request.getTitle() != null) target.setTitle(request.getTitle());
        if (request.getDescription() != null) target.setDescription(request.getDescription());
        if (request.getCover() != null) target.setCover(request.getCover());
    }
}
