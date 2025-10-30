import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;

public class GetActivitiesRestTest {

    @Test
    void testGetActivities() {
        // Configuração base do RestAssured (sem precisar criar cliente manual)
        RestAssured.baseURI = "https://fakerestapi.azurewebsites.net";

        given()
                .header("Accept", "text/plain; v=1.0") // Atribuindo o header da requisição
            .when()
                .get("/api/v1/Activities") // Requisição Get para o endpoint
            .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .contentType("application/json; charset=utf-8; v=1.0")
                // 👇 Validações do primeiro elemento do array JSON
                .body("[0].id", not(equalTo(0)))  // Id não deve ser zero
                .body("[0].title", not(isEmptyOrNullString()))   // Title não deve estar vazio
                .body("[0].dueDate", not(isEmptyOrNullString())); // DueDate não deve estar vazio
    }

}
