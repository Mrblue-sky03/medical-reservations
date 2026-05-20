package edu.unimag.domine.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import edu.unimag.domine.api.dto.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ErrorResponse> handleBussinessException(BussinessException ex) {
        var body = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
