package br.com.inovagab.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Testes de API - Monitoramento e Health Check (Spring Boot Actuator)")
public class ActuatorHealthApiTest extends BaseApiTest {

    @Test
    @DisplayName("GET /actuator/health - Verificação de saúde da aplicação (200 OK + JSON Schema)")
    void deveRetornarStatusUpNoHealthCheck() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body(matchesJsonSchemaInClasspath("schemas/health-schema.json"));
    }
}
