package com.feedback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        var body = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}