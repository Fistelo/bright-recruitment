package scanarios;

import static io.restassured.config.RestAssuredConfig.config;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import javax.annotation.Resource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rds.brightrecruitment.BrightRecruitmentApplication;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = BrightRecruitmentApplication.class,
    webEnvironment = DEFINED_PORT)
@ActiveProfiles("itest")
public class BaseITest {

  @LocalServerPort
  private int port;
  @Resource
  private Flyway flyway;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
    RestAssured.config = config().objectMapperConfig(getObjectMapperConfig());
  }

  private static ObjectMapperConfig getObjectMapperConfig() {
    final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    return new ObjectMapperConfig().jackson2ObjectMapperFactory((aClass, s) -> objectMapper);
  }

//  @AfterEach
//  public void cleanup() {
//    flyway.clean();
//    flyway.migrate();
//  }
}
