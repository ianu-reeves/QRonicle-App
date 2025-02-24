package com.qronicle.advice;

import com.qronicle.exception.*;
import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @Value("${app.upload.image.size}")
    private int MAX_FILE_SIZE;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        GenericErrorResponse response = new GenericErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GenericErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        GenericErrorResponse response = new GenericErrorResponse(
            HttpStatus.CONFLICT.value(),
            e.getMessage(),
            System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errors,
            System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<GenericErrorResponse> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException e) {
        GenericErrorResponse response = new GenericErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<GenericErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        GenericErrorResponse response = new GenericErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<GenericErrorResponse> handleMissingRefreshTokenException(MissingRefreshTokenException e) {
        return new ResponseEntity<>(
            new GenericErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                System.currentTimeMillis()
            ),
            HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<GenericErrorResponse> uploadTooLargeException(Exception e) {
        return new ResponseEntity<>(
            new GenericErrorResponse(
                413,
                "The maximum file size is " + MAX_FILE_SIZE + " bytes",
                System.currentTimeMillis()),
            HttpStatus.valueOf(413)
        );
    }

    @ExceptionHandler
    public ResponseEntity<GenericErrorResponse> handleBadRequest(Exception e) {
        boolean isConstraintViolation = e.getCause() instanceof ConstraintViolationException;
        boolean isJwtException = e.getCause() instanceof JwtException;
        String message = e.getMessage();
        if (isConstraintViolation) {
            message = "There was an error processing your request. Please ensure the data you submitted is valid";
        }
        if (isJwtException) {
            message = "There was an error processing the request. Please sign in again or try again later";
        }
        GenericErrorResponse response = new GenericErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
