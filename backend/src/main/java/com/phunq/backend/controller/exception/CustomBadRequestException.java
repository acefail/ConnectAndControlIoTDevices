package com.phunq.backend.controller.exception;

/**
 * @author phunq3107
 * @since 2/25/2022
 */
public class CustomBadRequestException extends Exception{
  private final String message;

  public CustomBadRequestException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
