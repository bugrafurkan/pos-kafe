package com.kafe.api.config;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  record Err(String message, Map<String,String> fields) { }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Err> handleValidation(MethodArgumentNotValidException ex) {
    Map<String,String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
      .forEach(e -> fields.put(e.getField(), e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(new Err("Validation failed", fields));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Err> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body(new Err("Type mismatch: " + ex.getMessage(), Map.of()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Err> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Err(ex.getMessage(), Map.of()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Err> handleIllegal(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(new Err(ex.getMessage(), Map.of()));
  }
}
