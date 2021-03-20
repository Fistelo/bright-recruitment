package com.rds.brightrecruitment.model;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {

  @NotEmpty(message = "Content of a comment shouldn't be empty")
  private String content;

}
