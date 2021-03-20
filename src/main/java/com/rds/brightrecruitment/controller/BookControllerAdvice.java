package com.rds.brightrecruitment.controller;

import static com.rds.brightrecruitment.exception.ApiError.INVALID_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rds.brightrecruitment.exception.ApiError;
import com.rds.brightrecruitment.exception.ApiErrorBody;
import com.rds.brightrecruitment.exception.BookApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class BookControllerAdvice {

  @ResponseBody
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiErrorBody handleValidationExceptions(final MethodArgumentNotValidException ex) {
    final List<ObjectError> errorList = ex.getBindingResult().getAllErrors();
    if (errorList.isEmpty()) {
      return new ApiErrorBody(INVALID_REQUEST.getCode(), INVALID_REQUEST.getErrorMessage());
    }
    return new ApiErrorBody(INVALID_REQUEST.getCode(), errorList.get(0).getDefaultMessage());
  }

  @ResponseBody
  @ExceptionHandler(BookApiException.class)
  public ApiErrorBody handleValidationExceptions(final BookApiException apiException, final HttpServletResponse response) {
    log.warn("Handling exception by API advisor: " + apiException.getMessage());
    response.setStatus(apiException.getStatus());
    return new ApiErrorBody(apiException.getCode(), apiException.getMessage());
  }
}
