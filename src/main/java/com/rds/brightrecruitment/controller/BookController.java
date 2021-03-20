package com.rds.brightrecruitment.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rds.brightrecruitment.model.AddCommentDto;
import com.rds.brightrecruitment.model.BookDto;
import com.rds.brightrecruitment.model.BookWithCommentsDto;
import com.rds.brightrecruitment.model.UpdateBookDto;
import com.rds.brightrecruitment.model.validation.OnAddValidation;
import com.rds.brightrecruitment.model.validation.OnUpdateValidation;
import com.rds.brightrecruitment.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BookController.BOOKS_PATH)
@RequiredArgsConstructor
public class BookController {

  static final String BOOKS_PATH = "/v1/books";

  private final BookService bookService;

  @GetMapping
  public Page<BookWithCommentsDto> fetchBooksWithComments(final Pageable pageable) {
    return bookService.getBooks(pageable);
  }

  @GetMapping("/{id}")
  public BookWithCommentsDto fetchBookWithComments(@PathVariable final long id) {
    return bookService.getBook(id);
  }

  @PostMapping
  public ResponseEntity<BookDto> addNewBook(@Validated(OnAddValidation.class) @RequestBody final UpdateBookDto bookDto) {
    final BookDto savedBookDto = bookService.addBook(bookDto);
    return ResponseEntity.created(URI.create(BOOKS_PATH + "/" + savedBookDto.getId())).body(savedBookDto);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookDto> updateBook(@PathVariable final long id, @Validated(OnUpdateValidation.class) @RequestBody final UpdateBookDto bookDto) {
    final BookDto savedBookDto = bookService.updateBook(id, bookDto);
    return ResponseEntity.created(URI.create(BOOKS_PATH + "/" + savedBookDto.getId())).body(savedBookDto);
  }

  @ResponseStatus(NO_CONTENT)
  @DeleteMapping("/{id}")
  public void removeBook(@PathVariable final long id) {
    bookService.removeBook(id);
  }

  @ResponseStatus(CREATED)
  @PostMapping("/{bookId}/comments")
  public void addComment(@PathVariable final long bookId, @Valid @RequestBody final AddCommentDto addCommentDto) {
    bookService.addCommentToBook(bookId, addCommentDto);
  }
}
