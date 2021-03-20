package com.rds.brightrecruitment.service;

import static com.rds.brightrecruitment.persistence.entities.Book.PREVIEW_COMMENTS_NUMBER;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rds.brightrecruitment.model.BookWithCommentsDto;
import com.rds.brightrecruitment.model.CommentDto;
import com.rds.brightrecruitment.persistence.entities.Book;

@Service
public class BookWithCommentsMapper {

  public BookWithCommentsDto map(final Book book) {
    return BookWithCommentsDto.builder()
        .id(book.getId())
        .author(book.getAuthor())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .numberOfPages(book.getNumberOfPages())
        .rating(book.getRating())
        .comments(mapLastComments(book))
        .build();
  }

  private List<CommentDto> mapLastComments(final Book book) {
    return book.getComments().stream()
        .map(comment -> new CommentDto(comment.getId(), comment.getContent()))
        .limit(PREVIEW_COMMENTS_NUMBER)
        .collect(Collectors.toList());
  }
}
