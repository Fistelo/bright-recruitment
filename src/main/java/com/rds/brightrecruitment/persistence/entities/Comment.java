package com.rds.brightrecruitment.persistence.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "comments")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String content;

  private LocalDateTime createdAt;

  @ManyToOne
  private Book book;

  public Comment(final String content) {
    this.content = content;
    this.createdAt = LocalDateTime.now();
  }
}
