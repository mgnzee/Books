package ru.vladmz.books.exceptions;

public class AlreadySubscribedException extends RuntimeException {
    public AlreadySubscribedException(Integer userId, Integer user2Id) {
        super("User with id " + userId + " already subscribed on user with id: " + user2Id);
    }
}
