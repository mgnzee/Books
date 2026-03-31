package ru.vladmz.books.services;

import ru.vladmz.books.entities.User;

public interface Ownable {
    User getOwner();
    Integer getId();
}
