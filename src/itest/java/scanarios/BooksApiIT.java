package scanarios;


import static com.rds.brightrecruitment.exception.ApiError.INVALID_REQUEST;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_AUTHOR;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_ISBN;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_NUMBER_OF_PAGES;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_RATING;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_TITLE;
import static com.rds.brightrecruitment.service.TestConstants.COMMENT_CONTENT;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;

import com.rds.brightrecruitment.model.AddCommentDto;
import com.rds.brightrecruitment.model.UpdateBookDto;
import com.rds.brightrecruitment.persistence.entities.Book;
import com.rds.brightrecruitment.persistence.repositories.BooksRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class BooksApiIT extends BaseITest {

  private static final String BOOKS_BASE_PATH = "/v1/books";

  @Resource
  private BooksRepository bookRepository;

  @Test
  void should_return_bad_request_on_invalid_isbn() {
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .title(BOOK_TITLE)
        .isbn("invalid-isbn")
        .build();

    final ValidatableResponse response = sendPostRequest(BOOKS_BASE_PATH, updateBookDto);

    validateErrorResponse(response, INVALID_REQUEST.getStatusCode().value(), INVALID_REQUEST.getCode(), "The isbn field needs to be a valid ISBN13");
  }

  @Test
  void should_return_bad_request_when_title_not_provided() {
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .build();

    final ValidatableResponse response = sendPostRequest(BOOKS_BASE_PATH, updateBookDto);

    validateErrorResponse(response, INVALID_REQUEST.getStatusCode().value(), INVALID_REQUEST.getCode(), "Book title cannot be empty");
  }

  @Test
  void should_add_new_book() {
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .title(BOOK_TITLE)
        .author(BOOK_AUTHOR)
        .isbn(BOOK_ISBN)
        .numberOfPages(BOOK_NUMBER_OF_PAGES)
        .rating(BOOK_RATING)
        .build();

    sendPostRequest(BOOKS_BASE_PATH, updateBookDto)
        .statusCode(SC_CREATED)
        .body("title", equalTo(BOOK_TITLE))
        .body("numberOfPages", equalTo(BOOK_NUMBER_OF_PAGES))
        .body("author", equalTo(BOOK_AUTHOR))
        .body("rating", equalTo(BOOK_RATING))
        .body("isbn", equalTo(BOOK_ISBN));
  }

  @Test
  void should_update_existing_book() {
    addBookManually();
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .rating(1)
        .build();

    createRequestBaseSpec()
        .given()
        .body(updateBookDto)
        .when().patch(BOOKS_BASE_PATH + "/1")
        .then()
        .statusCode(SC_CREATED)
        .body("title", equalTo(BOOK_TITLE))
        .body("rating", equalTo(1));
  }

  @Test
  void should_remove_existing_book() {
    addBookManually();

    createRequestBaseSpec()
        .given()
        .when().delete(BOOKS_BASE_PATH + "/1")
        .then()
        .statusCode(SC_NO_CONTENT);

    assertThat(bookRepository.findAll().iterator().hasNext()).isFalse();
  }

  @Test
  void should_add_comment() {
    addBookManually();

    sendPostRequest(BOOKS_BASE_PATH + "/1/comments", new AddCommentDto(COMMENT_CONTENT))
        .statusCode(SC_OK);

    assertThat(bookRepository.findAll().iterator().next().getComments().get(0).getContent()).isEqualTo(COMMENT_CONTENT);
  }

  private void addBookManually() {
    final Book book = new Book();
    book.setTitle(BOOK_TITLE);
    book.setRating(BOOK_RATING);
    bookRepository.save(book);
  }

  private ValidatableResponse sendPostRequest(final String url, final Object body) {
    return createRequestBaseSpec()
        .given()
        .body(body)
        .when().post(url)
        .then();
  }

  private RequestSpecification createRequestBaseSpec() {
    return given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON);
  }

  private void validateErrorResponse(final ValidatableResponse response, final int status, final String error, final String errorMessage) {
    response
        .statusCode(status)
        .body("code", equalTo(error))
        .body("message", equalTo(errorMessage));
  }
}
