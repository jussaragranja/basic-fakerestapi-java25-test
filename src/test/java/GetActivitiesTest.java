import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class GetActivitiesTest {

    // Record do Java 25: substitui o antigo "POJO" com menos código (equivalente ao struct em Go)
    record Activity(
            int id,
            String title,
            String dueDate,
            boolean completed
    ) {}

    @Test
    void testGetActivities() {
        // Configuração base do RestAssured (sem precisar criar cliente manual)
        RestAssured.baseURI = "https://fakerestapi.azurewebsites.net";

        // Enviando a requisição GET e salvando resposta em uma variavel pra guardar o objeto Response
        Response response = RestAssured
                .given() // Inicia a request
                    .header("Accept", "text/plain; v=1.0") // Atribuindo o header da requisição
                .when()
                    .get("/api/v1/Activities") // Requisição Get para o endpoint
                .then()
                    .log().all()
                    .extract().response(); // Extrai a resposta completa

        // ✅ Validação do Status code
        assertThat(response.statusCode())
                .as("Status HTTP deve ser 200")
                .isEqualTo(200);

        // ✅ Validação do Content-Type
        assertThat(response.contentType())
                .as("Content-Type deve conter application/json")
                .contains("application/json");

        // ✅ Desserializa JSON direto para lista de objetos Activity
        List<Activity> activities = response.jsonPath().getList(".", Activity.class);

        // ✅ Garante que retornou ao menos uma atividade
        assertThat(activities)
                .as("Deve retornar pelo menos uma atividade")
                .isNotEmpty();

        // ✅ Valida o primeiro item
        var first = activities.getFirst();
        assertThat(first.id()).as("Id não deve ser zero").isNotZero();
        assertThat(first.title()).as("Title não deve estar vazio").isNotBlank();
        assertThat(first.dueDate()).as("DueDate não deve estar vazio").isNotBlank();

        // ✅ Valida formato RFC3339
        assertThatCode(() -> OffsetDateTime.parse(first.dueDate()))
                .as("DueDate deve estar em formato RFC3339")
                .doesNotThrowAnyException();

        // ✅ Valida todos os itens
        activities.forEach(act -> {
            assertThat(act.id()).isNotZero();
            assertThat(act.title()).isNotBlank();
            assertThat(act.dueDate()).isNotBlank();
            assertThatCode(() -> OffsetDateTime.parse(act.dueDate()))
                    .doesNotThrowAnyException();
        });
    }
}
