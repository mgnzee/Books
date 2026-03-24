package ru.vladmz.books.exceptions;

public class UserNotFoundException extends ResourceNotFoundException{

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Integer userId) {
        super("User not found with id: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
