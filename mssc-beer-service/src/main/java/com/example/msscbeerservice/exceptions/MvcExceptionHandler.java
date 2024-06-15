package com.example.msscbeerservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MvcExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exception(Exception e) {
    return ResponseEntity.internalServerError().body(e.getMessage());
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<List<String>> validationErrorHandler(ConstraintViolationException ex) {
    List<String> errors = ex.getConstraintViolations().stream().map(Object::toString).toList();
    return ResponseEntity.badRequest().body(errors);
  }
}
