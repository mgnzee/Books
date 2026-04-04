package ru.vladmz.books.exceptions;

public class SelfSubscriptionException extends RuntimeException {
    public SelfSubscriptionException(String message) {
        super(message);
    }
}
