package com.rds.brightrecruitment.exception;

import lombok.Getter;

@Getter
public class BookApiException extends RuntimeException {

  private final ApiError error;

  public BookApiException(final ApiError apiError) {
    super(apiError.getErrorMessage());

    this.error = apiError;
  }

  public int getStatus() {
    return error.getStatusCode().value();
  }

  public String getCode() {
    return error.getCode();
  }

  public String getMessage() {
    return error.getErrorMessage();
  }
}
