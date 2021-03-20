package com.rds.brightrecruitment.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Table(name = "books")
@Data
@Entity
public class Book {

  public static final int PREVIEW_COMMENTS_NUMBER = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = AccessLevel.NONE)
  private Long id;

  private String title;

  private String author;

  private String isbn;

  private Integer numberOfPages;

  private Integer rating;

  @OrderBy("createdAt DESC")
  @BatchSize(size = PREVIEW_COMMENTS_NUMBER)
  @OneToMany(mappedBy = "book", cascade = CascadeType.MERGE, orphanRemoval = true)
  @Setter(value = AccessLevel.NONE)
  private List<Comment> comments = new ArrayList<>();

  public void addComment(final Comment comment) {
    comment.setBook(this);
    comments.add(comment);
  }
}
