package com.bridgetrack.bridgetrack.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

   

    private Map<String, Object> body(HttpStatus status, String message, String path) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("timestamp", Instant.now().toString());
        b.put("status", status.value());
        b.put("error", status.getReasonPhrase());
        b.put("message", message);
        if (path != null) b.put("path", path);
        return b;
    }

    

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(body(status, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidPrerequisiteException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            InvalidPrerequisiteException ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(body(status, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(PrerequisiteAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            PrerequisiteAlreadyExistsException ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT; 
        return ResponseEntity.status(status)
                .body(body(status, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(DuplicateStudentException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateStudent(
            DuplicateStudentException ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT; 
        return ResponseEntity.status(status)
                .body(body(status, ex.getMessage(), request.getRequestURI()));
    }
    
    @ExceptionHandler(PrerequisiteNotMetException.class)
    public ResponseEntity<Map<String, Object>> handlePrerequisiteNotMet(
            PrerequisiteNotMetException ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(body(status, ex.getMessage(), request.getRequestURI()));
    }

    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(body(status, "Something went wrong.", request.getRequestURI()));
    }
}