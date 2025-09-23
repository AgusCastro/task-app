package com.challenge.taskapp.exception;

import com.challenge.taskapp.dto.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(final MethodArgumentNotValidException ex, final WebRequest request) {
        log.debug("Handling MethodArgumentNotValidException: {}", ex.getMessage(), ex);

        final Optional<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> "Field: " + error.getField() + " -> " + error.getDefaultMessage())
                .collect(Collectors.joining(", ")).describeConstable();

        final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST, errors.orElseGet(() -> ex.getBody().getDetail()), request);

        log.debug("Returning BAD_REQUEST response for validation errors: {}", errors.orElse("Unknown validation error"));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.debug("Handling MethodArgumentTypeMismatchException: {}, Required type: {}", ex.getMessage(), ex.getRequiredType(), ex);

        // Send 404 (resource not found) when UUID conversion fails
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
            log.debug("Returning NOT_FOUND response for UUID conversion failure");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        log.debug("Returning BAD_REQUEST response for type mismatch");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.debug("Handling NotFoundException: {}", ex.getMessage(), ex);

        final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);

        log.debug("Returning NOT_FOUND response for NotFoundException");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MissingTenantIdException.class)
    public ResponseEntity<CustomErrorResponse> handleMissingTenantIdException(MissingTenantIdException ex, WebRequest request) {
        log.debug("Handling MissingTenantIdException: {}", ex.getMessage(), ex);

        final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

        log.debug("Returning BAD_REQUEST response for MissingTenantIdException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericException(WebRequest request) {
        log.debug("Handling generic Exception - Internal server error occurred");

        final CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error.", request);

        log.debug("Returning INTERNAL_SERVER_ERROR response for generic exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
