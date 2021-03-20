package com.rds.brightrecruitment.service;

import static com.rds.brightrecruitment.persistence.entities.Book.PREVIEW_COMMENTS_NUMBER;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_AUTHOR;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_TITLE;
import static com.rds.brightrecruitment.service.TestConstants.COMMENT_CONTENT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.rds.brightrecruitment.model.BookWithCommentsDto;
import com.rds.brightrecruitment.persistence.entities.Book;
import com.rds.brightrecruitment.persistence.entities.Comment;

class BookWithCommentsMapperTest {

  private final BookWithCommentsMapper bookWithCommentsMapper = new BookWithCommentsMapper();

  @Test
  void should_map_book_with_exact_amout_of_last_comments() {
    final Book book = new Book();
    book.setAuthor(BOOK_AUTHOR);
    book.setTitle(BOOK_TITLE);
    addMultipleBookComments(book);

    final BookWithCommentsDto bookDto = bookWithCommentsMapper.map(book);

    assertThat(bookDto.getAuthor()).isEqualTo(BOOK_AUTHOR);
    assertThat(bookDto.getTitle()).isEqualTo(BOOK_TITLE);
    assertThat(bookDto.getComments().size()).isEqualTo(PREVIEW_COMMENTS_NUMBER);
  }

  private void addMultipleBookComments(final Book book) {
    for (int i = 0; i < PREVIEW_COMMENTS_NUMBER + 2; i++) {
      book.addComment(new Comment(COMMENT_CONTENT));
    }
  }
}