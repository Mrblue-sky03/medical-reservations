package edu.unimag.domine.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import edu.unimag.domine.api.dto.ErrorResponse;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBussinessException(BusinessException ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(), request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();

        var body = ErrorResponse.of(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields", request.getDescription(false), violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request){
        var body= ErrorResponse.of(HttpStatus.BAD_REQUEST, "Invalid request body", request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(ResourceNotFoundException ex, WebRequest request) {
        var body = ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}