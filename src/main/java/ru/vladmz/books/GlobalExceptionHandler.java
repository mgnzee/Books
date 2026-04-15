package ru.vladmz.books;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.vladmz.books.exceptions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

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
    public ResponseEntity<ErrorResponse> handleBadRequest(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        StringBuilder sb = new StringBuilder();
        errors.forEach((key, value) -> sb.append(key).append(" : ").append(value).append("; "));
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad request",
                        sb.toString()
                )
        );
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

    @ExceptionHandler(CommentAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleCommentDeleted(CommentAlreadyDeletedException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(generateResponse(ex, HttpStatus.BAD_REQUEST, "Comment is already deleted"));
    }

    @ExceptionHandler(ResourceAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleResourceDeleted(ResourceAlreadyDeletedException ex){
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex){
        StringBuilder message = new StringBuilder();
        message.append("Method ").append(ex.getMethod()).append(" not allowed here. ").append("Allowed methods: ");
        Optional.ofNullable(ex.getSupportedHttpMethods())
                .ifPresent( methods -> methods.forEach(m -> message.append(m).append(" ")));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(generateResponse(ex, HttpStatus.METHOD_NOT_ALLOWED, message.toString()));
    }



    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex){
        logger.log(Level.SEVERE, "Runtime Exception occurred", ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An Internal Server Error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        logger.log(Level.SEVERE, "Exception occurred", ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An Internal Server Error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}