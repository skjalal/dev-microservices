package com.example.beerorderservice.exceptions;

import java.io.Serial;

public class NotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 13232323L;

  public NotFoundException(String message) {
    super(message);
  }
}
