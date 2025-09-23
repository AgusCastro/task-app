package com.challenge.taskapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // If the failed conversion was for a UUID parameter, treat it as a 404 (resource not found)
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        // For other type mismatches, return 400 (bad request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input format");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
