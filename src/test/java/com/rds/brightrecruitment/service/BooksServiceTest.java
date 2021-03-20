package com.rds.brightrecruitment.service;

import static com.rds.brightrecruitment.exception.ApiError.ENTITY_NOT_FOUND;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_AUTHOR;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_ID;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_ISBN;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_NUMBER_OF_PAGES;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_RATING;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_TITLE;
import static com.rds.brightrecruitment.service.TestConstants.COMMENT_CONTENT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.rds.brightrecruitment.exception.BookApiException;
import com.rds.brightrecruitment.model.AddCommentDto;
import com.rds.brightrecruitment.model.BookDto;
import com.rds.brightrecruitment.model.BookWithCommentsDto;
import com.rds.brightrecruitment.model.UpdateBookDto;
import com.rds.brightrecruitment.persistence.entities.Book;
import com.rds.brightrecruitment.persistence.repositories.BooksRepository;

@ExtendWith(MockitoExtension.class)
class BooksServiceTest {

  @Mock
  private BooksRepository booksRepository;
  @Mock
  private ModelMapper modelMapper;
  @Mock
  private BookWithCommentsMapper bookWithCommentsMapper;
  @InjectMocks
  private BooksService booksService;

  @Mock
  private Book savedBook;
  @Mock
  private BookDto bookDto;

  private Book book;

  @BeforeEach
  public void setUp() {
    book = new Book();
    book.setAuthor(BOOK_AUTHOR);
    book.setTitle(BOOK_TITLE);
    book.setIsbn(BOOK_ISBN);
    book.setNumberOfPages(BOOK_NUMBER_OF_PAGES);
    book.setRating(BOOK_RATING);
  }

  @Test
  void should_retrieve_books() {
    final Pageable pageable = mock(Pageable.class);
    final BookWithCommentsDto bookWithCommentsDto = mock(BookWithCommentsDto.class);
    when(booksRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(book)));
    when(bookWithCommentsMapper.map(book)).thenReturn(bookWithCommentsDto);

    final Page<BookWithCommentsDto> bookPage = booksService.getBooks(pageable);

    assertThat(bookPage.getContent().get(0)).isEqualTo(bookWithCommentsDto);
  }

  @Test
  void should_add_book() {
    final UpdateBookDto updateBookDto = mock(UpdateBookDto.class);
    when(modelMapper.map(updateBookDto, Book.class)).thenReturn(book);
    mockSaveAndMapToDto();

    final BookDto addedBook = booksService.addBook(updateBookDto);

    assertThat(addedBook).isEqualTo(bookDto);
  }

  @Test
  void should_update_book() {
    mockBookFound();
    mockSaveAndMapToDto();
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .title("Invincible")
        .author("Stanislaw Lem")
        .isbn("978-83-89405-00-5")
        .numberOfPages(109)
        .rating(4)
        .build();

    final BookDto updatedBook = booksService.updateBook(BOOK_ID, updateBookDto);

    assertThat(updatedBook).isEqualTo(bookDto);
    assertThat(book.getTitle()).isEqualTo("Invincible");
    assertThat(book.getAuthor()).isEqualTo("Stanislaw Lem");
    assertThat(book.getIsbn()).isEqualTo("978-83-89405-00-5");
    assertThat(book.getNumberOfPages()).isEqualTo(109);
    assertThat(book.getRating()).isEqualTo(4);
  }

  @Test
  void should_not_update_book_when_no_value_provided() {
    mockBookFound();
    mockSaveAndMapToDto();
    final UpdateBookDto updateBookDto = new UpdateBookDto();

    final BookDto updatedBook = booksService.updateBook(BOOK_ID, updateBookDto);

    assertThat(updatedBook).isEqualTo(bookDto);
  }

  @Test
  void should_remove_book() {
    mockBookFound();

    booksService.removeBook(BOOK_ID);

    verify(booksRepository).delete(book);
  }

  @Test
  void should_add_comment_to_a_book() {
    mockBookFound();
    final AddCommentDto addCommentDto = new AddCommentDto(COMMENT_CONTENT);

    booksService.addCommentToBook(BOOK_ID, addCommentDto);

    verify(booksRepository).save(book);
    assertThat(book.getComments().get(0).getContent()).isEqualTo(COMMENT_CONTENT);
  }

  @Test
  void should_throw_application_specific_exception_on_book_not_found() {
    when(booksRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

    final BookApiException exception = assertThrows(BookApiException.class, () -> booksService.removeBook(BOOK_ID));

    assertThat(exception.getError()).isEqualTo(ENTITY_NOT_FOUND);
  }

  private void mockBookFound() {
    when(booksRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
  }

  private void mockSaveAndMapToDto() {
    when(booksRepository.save(book)).thenReturn(savedBook);
    when(modelMapper.map(savedBook, BookDto.class)).thenReturn(bookDto);
  }
}