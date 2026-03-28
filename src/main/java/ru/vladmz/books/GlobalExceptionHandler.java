package ru.vladmz.books;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vladmz.books.exceptions.ErrorResponse;
import ru.vladmz.books.exceptions.ResourceNotFoundException;
import ru.vladmz.books.exceptions.UserNotAuthenticatedException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse generateResponse(Exception ex, HttpStatus status, String message){
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(generateResponse(ex, HttpStatus.NOT_FOUND, "Resource not found"));
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuth(UserNotAuthenticatedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                body(generateResponse(ex, HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }
}