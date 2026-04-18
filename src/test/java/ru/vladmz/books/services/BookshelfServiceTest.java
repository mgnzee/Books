package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.repositories.BookRepository;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

@ExtendWith(MockitoExtension.class)
public class BookshelfServiceTest {

    @Mock
    BookshelfRepository bookshelfRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    PermissionChecker permissionChecker;
    @Mock
    CurrentUserProvider provider;

    @InjectMocks
    BookshelfService bookshelfService;

    Bookshelf bookshelf;
    User user;

    @BeforeEach
    void setUp(){

    }

    @Test
    void findAll(){

    }

    @Test
    void findById(){

    }
}
