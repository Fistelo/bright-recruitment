package com.rds.brightrecruitment.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiError {

  INVALID_REQUEST(BAD_REQUEST, "invalid_request", "The provided request body is invalid"),
  ENTITY_NOT_FOUND(NOT_FOUND, "not_found", "The entity with provided ID wasn't found");

  private final HttpStatus statusCode;
  private final String code;
  private final String errorMessage;
}
