package com.rds.brightrecruitment.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rds.brightrecruitment.model.validation.OnAddValidation;
import com.rds.brightrecruitment.model.validation.OnUpdateValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpdateBookDto {

  @NotBlank(message = "Book title cannot be empty", groups = { OnAddValidation.class })
  private String title;

  private String author;

  @ISBN(type = ISBN.Type.ISBN_13, message = "The isbn field needs to be a valid ISBN13", groups = { OnAddValidation.class, OnUpdateValidation.class })
  private String isbn;

  @Min(value = 1, message = "A book must have at least one page", groups = { OnAddValidation.class, OnUpdateValidation.class })
  private Integer numberOfPages;

  @Range(min = 1, max = 5, message = "The rating need to be between 1 and 5", groups = { OnAddValidation.class, OnUpdateValidation.class })
  private Integer rating;

}
