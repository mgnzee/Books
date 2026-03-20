package ru.vladmz.books.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookshelves/{bookshelfId}/comments")
public class BookshelfCommentController {
}
