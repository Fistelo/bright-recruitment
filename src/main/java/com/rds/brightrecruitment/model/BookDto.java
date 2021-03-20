package com.rds.brightrecruitment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BookDto extends UpdateBookDto {

  private Long id;

}
