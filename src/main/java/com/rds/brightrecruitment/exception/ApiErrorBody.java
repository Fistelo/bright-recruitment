package com.rds.brightrecruitment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorBody {

  private final String code;
  private final String message;

}
