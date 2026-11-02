package br.com.inovagab.api;

import br.com.inovagab.dto.request.GrupoRequest;
import br.com.inovagab.dto.request.MembroRequest;
import br.com.inovagab.model.Grupo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API - Gestão de Squads e Governança (GestorGrupoController)")
public class GestorGrupoApiTest extends BaseApiTest {

    private TestUserAuth gestorAuth;
    private TestUserAuth colaboradorAuth;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        gestorAuth = createAndAuthenticateGestor("gestor.squads@aguabranca.com", "senha123");
        colaboradorAuth = createAndAuthenticateColaborador("colab.squads@aguabranca.com", "senha123");
    }

    @Test
    @DisplayName("POST /api/gestor/grupos - Gestor cria Squad ESG com sucesso (201 Created + JSON Schema)")
    void deveCriarGrupoComSucessoEValidarSchema() {
        GrupoRequest request = new GrupoRequest();
        request.setNome("Squad Ecoeficiência Operacional");
        request.setDepartamento("Sustentabilidade");
        request.setDescricao("Squad dedicada à redução da pegada de carbono e eficiência energética");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(request)
        .when()
            .post("/api/gestor/grupos")
        .then()
            .statusCode(201)
            .body("nome", equalTo("Squad Ecoeficiência Operacional"))
            .body("hashId", notNullValue())
            .body("gestorId", equalTo(gestorAuth.usuario().getId()))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-response-schema.json"));
    }

    @Test
    @DisplayName("POST /api/gestor/grupos - Colaborador sem perfil GESTOR deve receber 400 Bad Request")
    void deveNegarCriacaoDeGrupoParaNaoGestor() {
        GrupoRequest request = new GrupoRequest();
        request.setNome("Squad Não Permitida");
        request.setDepartamento("TI");
        request.setDescricao("Tentativa sem privilégio de gestor");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + colaboradorAuth.token())
            .body(request)
        .when()
            .post("/api/gestor/grupos")
        .then()
            .statusCode(400)
            .body("error", containsString("exclusivo para usuários com perfil GESTOR"))
            .body(matchesJsonSchemaInClasspath("schemas/error-response-schema.json"));
    }

    @Test
    @DisplayName("GET /api/gestor/grupos - Listar Squads do gestor com sucesso (200 OK + JSON Schema)")
    void deveListarGruposDoGestorEValidarSchema() {
        createSquadForGestor(gestorAuth.usuario(), "Squad Logística Verde");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/gestor/grupos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-list-schema.json"));
    }

    @Test
    @DisplayName("GET /api/gestor/grupos/{groupId} - Obter Squad por HashId com sucesso (200 OK + JSON Schema)")
    void deveObterGrupoPorHashIdEValidarSchema() {
        Grupo grupo = createSquadForGestor(gestorAuth.usuario(), "Squad Inclusão ESG");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/gestor/grupos/" + grupo.getHashId())
        .then()
            .statusCode(200)
            .body("nome", equalTo("Squad Inclusão ESG"))
            .body("hashId", equalTo(grupo.getHashId()))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-response-schema.json"));
    }

    @Test
    @DisplayName("PUT /api/gestor/grupos/{groupId} - Atualizar dados da Squad com sucesso (200 OK + JSON Schema)")
    void deveAtualizarGrupoComSucessoEValidarSchema() {
        Grupo grupo = createSquadForGestor(gestorAuth.usuario(), "Squad Antiga");

        GrupoRequest updateRequest = new GrupoRequest();
        updateRequest.setNome("Squad Sustentabilidade Renovada");
        updateRequest.setDepartamento("Governança ESG");
        updateRequest.setDescricao("Nova diretriz e metas atualizadas");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(updateRequest)
        .when()
            .put("/api/gestor/grupos/" + grupo.getHashId())
        .then()
            .statusCode(200)
            .body("nome", equalTo("Squad Sustentabilidade Renovada"))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-response-schema.json"));
    }

    @Test
    @DisplayName("POST /api/gestor/grupos/{groupId}/membros - Adicionar membro à Squad (200 OK + JSON Schema)")
    void deveAdicionarMembroComSucessoEValidarSchema() {
        Grupo grupo = createSquadForGestor(gestorAuth.usuario(), "Squad Diversidade");

        MembroRequest membroRequest = new MembroRequest();
        membroRequest.setEmail(colaboradorAuth.usuario().getEmail());
        membroRequest.setRole("OPERADOR");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(membroRequest)
        .when()
            .post("/api/gestor/grupos/" + grupo.getHashId() + "/membros")
        .then()
            .statusCode(200)
            .body("membros.email", hasItem(colaboradorAuth.usuario().getEmail()))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-response-schema.json"));
    }

    @Test
    @DisplayName("DELETE /api/gestor/grupos/{groupId}/membros/{email} - Remover membro da Squad (200 OK + JSON Schema)")
    void deveRemoverMembroComSucessoEValidarSchema() {
        Grupo grupo = createSquadForGestor(gestorAuth.usuario(), "Squad Economia Circular");

        // Adiciona membro primeiro
        MembroRequest membroRequest = new MembroRequest();
        membroRequest.setEmail(colaboradorAuth.usuario().getEmail());
        membroRequest.setRole("OPERADOR");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(membroRequest)
            .post("/api/gestor/grupos/" + grupo.getHashId() + "/membros")
            .then().statusCode(200);

        // Remove membro
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .delete("/api/gestor/grupos/" + grupo.getHashId() + "/membros/" + colaboradorAuth.usuario().getEmail())
        .then()
            .statusCode(200)
            .body("membros.email", not(hasItem(colaboradorAuth.usuario().getEmail())))
            .body(matchesJsonSchemaInClasspath("schemas/grupo-response-schema.json"));
    }

    @Test
    @DisplayName("GET /api/gestor/usuarios - Listar usuários disponíveis (200 OK + JSON Schema)")
    void deveListarUsuariosDisponiveisEValidarSchema() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/gestor/usuarios")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/usuarios-list-schema.json"));
    }
}
