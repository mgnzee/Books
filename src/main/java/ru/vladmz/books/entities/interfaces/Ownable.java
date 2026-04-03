package ru.vladmz.books.entities.interfaces;

import ru.vladmz.books.entities.User;

public interface Ownable {
    User getOwner();
    Integer getId();
}
