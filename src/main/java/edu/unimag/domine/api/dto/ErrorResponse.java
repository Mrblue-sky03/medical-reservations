package edu.unimag.domine.api.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING) OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> fieldViolations
) implements Serializable {

    public static ErrorResponse of(HttpStatus status, String message, String path, List<FieldViolation> violations) {
        return new ErrorResponse(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path, violations);
    }

    public record FieldViolation(
            String field,
            String message
    ) implements Serializable {}
}