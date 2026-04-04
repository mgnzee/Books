package ru.vladmz.books;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vladmz.books.exceptions.*;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValid(MethodArgumentNotValidException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateResponse(ex, HttpStatus.BAD_REQUEST, "Bad request"));
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuth(UserNotAuthenticatedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                body(generateResponse(ex, HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(generateResponse(ex, HttpStatus.FORBIDDEN, "Access denied"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledUser(DisabledException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(generateResponse(ex, HttpStatus.FORBIDDEN, "Account is deleted"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedUser(LockedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(generateResponse(ex, HttpStatus.FORBIDDEN, "Account is disabled"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(generateResponse(ex, HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthExc(AuthenticationException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(generateResponse(ex, HttpStatus.UNAUTHORIZED, "Authentication failed"));
    }

    @ExceptionHandler(CommentAlreadyDeleted.class)
    public ResponseEntity<ErrorResponse> handleCommentDeleted(CommentAlreadyDeleted ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateResponse(ex, HttpStatus.BAD_REQUEST, "Comment is already deleted"));
    }

    @ExceptionHandler(ResourceAlreadyDeleted.class)
    public ResponseEntity<ErrorResponse> handleResourceDeleted(ResourceAlreadyDeleted ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateResponse(ex, HttpStatus.BAD_REQUEST, "Resource is already deleted"));
    }

    @ExceptionHandler(AlreadySubscribedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadySubscribed(AlreadySubscribedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(generateResponse(ex, HttpStatus.CONFLICT, "User already subscribed"));
    }

    @ExceptionHandler(SelfSubscriptionException.class)
    public ResponseEntity<ErrorResponse> handleSelfSubscription(SelfSubscriptionException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(generateResponse(ex, HttpStatus.CONFLICT, "You can't subscribe to yourself"));
    }
}