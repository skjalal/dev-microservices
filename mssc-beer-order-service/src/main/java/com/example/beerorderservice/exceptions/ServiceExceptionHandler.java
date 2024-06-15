package com.example.beerorderservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServiceExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body(ex.getLocalizedMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGlobalException(Exception ex) {
    return ResponseEntity.internalServerError().body(ex.getLocalizedMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
  }

}
