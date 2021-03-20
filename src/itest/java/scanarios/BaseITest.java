package scanarios;

import static io.restassured.config.RestAssuredConfig.config;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rds.brightrecruitment.BrightRecruitmentApplication;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = BrightRecruitmentApplication.class,
    webEnvironment = DEFINED_PORT)
@ActiveProfiles("itest")
@Testcontainers
public class BaseITest {

  @LocalServerPort
  private int port;

  @Rule
  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("bright-db")
      .withUsername("test")
      .withPassword("test");

  @Rule
  @DynamicPropertySource
  static void registerDynamicProperties(final DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
    RestAssured.config = config().objectMapperConfig(getObjectMapperConfig());
  }

  private static ObjectMapperConfig getObjectMapperConfig() {
    final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    return new ObjectMapperConfig().jackson2ObjectMapperFactory((aClass, s) -> objectMapper);
  }

  @Test
  void verify_postgres_running() {
    assertThat(postgreSQLContainer.isRunning()).isTrue();
  }
}
