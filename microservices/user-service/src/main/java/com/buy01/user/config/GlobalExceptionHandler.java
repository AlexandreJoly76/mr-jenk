package com.buy01.user.config;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateKey(DuplicateKeyException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(Map.of("error", "Cet email est déjà utilisé !"));
    }
}