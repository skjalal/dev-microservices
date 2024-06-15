package com.example.msscbeerservice.exceptions;

import java.io.Serial;

public class NotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 2220745766939L;

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
