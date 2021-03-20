package com.rds.brightrecruitment.service;

import static com.rds.brightrecruitment.exception.ApiError.ENTITY_NOT_FOUND;
import static java.util.Optional.ofNullable;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rds.brightrecruitment.exception.BookApiException;
import com.rds.brightrecruitment.model.AddCommentDto;
import com.rds.brightrecruitment.model.BookDto;
import com.rds.brightrecruitment.model.BookWithCommentsDto;
import com.rds.brightrecruitment.model.UpdateBookDto;
import com.rds.brightrecruitment.persistence.entities.Book;
import com.rds.brightrecruitment.persistence.entities.Comment;
import com.rds.brightrecruitment.persistence.repositories.BookRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final ModelMapper modelMapper;
  private final BookWithCommentsMapper bookWithCommentsMapper;

  public Page<BookWithCommentsDto> getBooks(final Pageable pageable) {
    return bookRepository.findAll(pageable)
        .map(bookWithCommentsMapper::map);
  }

  public BookWithCommentsDto getBook(final long id) {
    final Book book = fetchBookById(id);
    return bookWithCommentsMapper.map(book);
  }

  public BookDto addBook(final UpdateBookDto updateBookDto) {
    final Book bookEntity = modelMapper.map(updateBookDto, Book.class);
    final Book savedBook = bookRepository.save(bookEntity);
    return modelMapper.map(savedBook, BookDto.class);
  }

  @Transactional
  public BookDto updateBook(final long id, final UpdateBookDto updateBookDto) {
    final Book bookEntity = fetchBookById(id);
    ofNullable(updateBookDto.getAuthor()).ifPresent(bookEntity::setAuthor);
    ofNullable(updateBookDto.getTitle()).ifPresent(bookEntity::setTitle);
    ofNullable(updateBookDto.getNumberOfPages()).ifPresent(bookEntity::setNumberOfPages);
    ofNullable(updateBookDto.getIsbn()).ifPresent(bookEntity::setIsbn);
    ofNullable(updateBookDto.getRating()).ifPresent(bookEntity::setRating);

    return modelMapper.map(bookEntity, BookDto.class);
  }

  public void removeBook(final long id) {
    bookRepository.delete(fetchBookById(id));
  }

  @Transactional
  public void addCommentToBook(final long bookId, final AddCommentDto addCommentDto) {
    final Book bookEntity = fetchBookById(bookId);
    bookEntity.addComment(new Comment(addCommentDto.getContent()));
    bookRepository.save(bookEntity);
  }

  private Book fetchBookById(final long id) {
    return bookRepository.findById(id).orElseThrow(() -> new BookApiException(ENTITY_NOT_FOUND));
  }
}
