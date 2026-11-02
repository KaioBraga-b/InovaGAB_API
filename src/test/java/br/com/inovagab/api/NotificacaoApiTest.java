package br.com.inovagab.api;

import br.com.inovagab.model.Notificacao;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API - Notificações Corporativas e Alertas ESG (NotificacaoController)")
public class NotificacaoApiTest extends BaseApiTest {

    private TestUserAuth gestorAuth;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        gestorAuth = createAndAuthenticateGestor("gestor.notificacoes@aguabranca.com", "senha123");
    }

    @Test
    @DisplayName("GET /api/notificacoes/{role} - Listar notificações para a Role com sucesso (200 OK + JSON Schema)")
    void deveListarNotificacoesPorRoleComSucesso() {
        Notificacao notificacao = new Notificacao();
        notificacao.setMensagem("Nova proposta de eficiência energética aguarda revisão");
        notificacao.setDestinatarioRole("GESTOR");
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setTipo("NOVA_IDEIA");
        notificacaoRepository.save(notificacao);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/notificacoes/GESTOR")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body("[0].destinatarioRole", equalTo("GESTOR"))
            .body("[0].lida", equalTo(false))
            .body(matchesJsonSchemaInClasspath("schemas/notificacoes-list-schema.json"));
    }

    @Test
    @DisplayName("PUT /api/notificacoes/{id}/lida - Marcar notificação como lida (200 OK + JSON Schema)")
    void deveMarcarNotificacaoComoLidaComSucesso() {
        Notificacao notificacao = new Notificacao();
        notificacao.setMensagem("Alerta de conformidade ESG");
        notificacao.setDestinatarioRole("GESTOR");
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao = notificacaoRepository.save(notificacao);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .put("/api/notificacoes/" + notificacao.getId() + "/lida")
        .then()
            .statusCode(200)
            .body("id", equalTo(notificacao.getId()))
            .body("lida", equalTo(true))
            .body(matchesJsonSchemaInClasspath("schemas/notificacao-schema.json"));
    }
}
