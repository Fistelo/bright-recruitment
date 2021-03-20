package scanarios;


import static com.rds.brightrecruitment.exception.ApiError.INVALID_REQUEST;
import static com.rds.brightrecruitment.persistence.entities.Book.PREVIEW_COMMENTS_NUMBER;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_AUTHOR;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_ISBN;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_NUMBER_OF_PAGES;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_RATING;
import static com.rds.brightrecruitment.service.TestConstants.BOOK_TITLE;
import static com.rds.brightrecruitment.service.TestConstants.COMMENT_CONTENT;
import static com.rds.brightrecruitment.service.TestConstants.LATEST_COMMENT_CONTENT;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;

import com.rds.brightrecruitment.model.AddCommentDto;
import com.rds.brightrecruitment.model.UpdateBookDto;
import com.rds.brightrecruitment.persistence.entities.Book;
import com.rds.brightrecruitment.persistence.entities.Comment;
import com.rds.brightrecruitment.persistence.repositories.BookRepository;
import com.rds.brightrecruitment.persistence.repositories.CommentRepository;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class BooksApiIT extends BaseITest {

  private static final String BOOKS_BASE_PATH = "/v1/books";

  @Resource
  private BookRepository bookRepository;

  @Resource
  private CommentRepository commentRepository;

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
  void should_return_bad_request_when_invalid_pages_number_provided_provided() {
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .title(BOOK_TITLE)
        .numberOfPages(-1)
        .build();

    final ValidatableResponse response = sendPostRequest(BOOKS_BASE_PATH, updateBookDto);

    validateErrorResponse(response, INVALID_REQUEST.getStatusCode().value(), INVALID_REQUEST.getCode(), "A book must have at least one page");
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

    final ValidatableResponse validatableResponse = sendPostRequest(BOOKS_BASE_PATH, updateBookDto)
        .statusCode(SC_CREATED);

    final int savedBookId = assertBook(validatableResponse)
        .extract()
        .body()
        .jsonPath()
        .getInt("id");
    assertBook(getBookById(savedBookId).statusCode(SC_OK));
  }

  private ValidatableResponse assertBook(final ValidatableResponse validatableResponse) {
    return validatableResponse
        .body("title", equalTo(BOOK_TITLE))
        .body("numberOfPages", equalTo(BOOK_NUMBER_OF_PAGES))
        .body("author", equalTo(BOOK_AUTHOR))
        .body("rating", equalTo(BOOK_RATING))
        .body("isbn", equalTo(BOOK_ISBN));
  }

  @Test
  void should_update_existing_book() {
    final Book book = addBookManually();
    final UpdateBookDto updateBookDto = UpdateBookDto.builder()
        .rating(1)
        .build();

    createRequestBaseSpec()
        .given()
        .body(updateBookDto)
        .when().patch(BOOKS_BASE_PATH + "/" + book.getId())
        .then()
        .statusCode(SC_CREATED)
        .body("title", equalTo(BOOK_TITLE))
        .body("rating", equalTo(1));

    getBookById(book.getId())
        .statusCode(SC_OK)
        .body("title", equalTo(BOOK_TITLE))
        .body("rating", equalTo(1));
  }

  @Test
  void should_remove_existing_book() {
    final Book book = addBookManually();

    createRequestBaseSpec()
        .given()
        .when().delete(BOOKS_BASE_PATH + "/" + book.getId())
        .then()
        .statusCode(SC_NO_CONTENT);

    getBookById(book.getId()).statusCode(SC_NOT_FOUND);
  }

  @Test
  void should_add_comment() {
    final Book book = addBookManually();

    sendPostRequest(BOOKS_BASE_PATH + "/" + book.getId() + "/comments", new AddCommentDto(COMMENT_CONTENT))
        .statusCode(SC_CREATED);

    getBookById(book.getId())
        .statusCode(SC_OK)
        .body("comments", hasSize(1))
        .body("comments[0].content", equalTo(COMMENT_CONTENT));
  }

  @Test
  void should_return_book_with_preview_comments() {
    final Book book = addBookWithMultipleComments();

    getBookById(book.getId())
        .statusCode(SC_OK)
        .body("title", equalTo(BOOK_TITLE))
        .body("comments", hasSize(5))
        .body("comments[0].content", equalTo(LATEST_COMMENT_CONTENT))
        .body("comments[1].content", equalTo(LATEST_COMMENT_CONTENT))
        .body("comments[2].content", equalTo(LATEST_COMMENT_CONTENT))
        .body("comments[3].content", equalTo(LATEST_COMMENT_CONTENT))
        .body("comments[4].content", equalTo(LATEST_COMMENT_CONTENT));
  }

  private Book addBookManually() {
    final Book book = new Book();
    book.setTitle(BOOK_TITLE);
    book.setRating(BOOK_RATING);
    return bookRepository.save(book);
  }

  private Book addBookWithMultipleComments() {
    final Book book = new Book();
    book.setTitle(BOOK_TITLE);
    final List<Comment> previousComments = addMultipleComments(3, COMMENT_CONTENT, book); // adding early comments
    final List<Comment> newerComments = addMultipleComments(PREVIEW_COMMENTS_NUMBER, LATEST_COMMENT_CONTENT, book); // adding early comments
    final Book savedBook = bookRepository.save(book);
    commentRepository.saveAll(previousComments);
    commentRepository.saveAll(newerComments);
    return savedBook;
  }

  private List<Comment> addMultipleComments(final int nComments, final String content, final Book book) {
    final List<Comment> commentList = new ArrayList<>();
    for (int i = 0; i < nComments; i++) {
      final Comment comment = new Comment(content);
      book.addComment(comment);
      commentList.add(comment);
    }
    return commentList;
  }

  private ValidatableResponse getBookById(final long bookId) {
    return createRequestBaseSpec()
        .given()
        .when().get(BOOKS_BASE_PATH + "/" + bookId)
        .then();
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
