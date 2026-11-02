package br.com.inovagab.api;

import br.com.inovagab.dto.request.LoginRequest;
import br.com.inovagab.dto.request.RegisterRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API - Autenticação e Segurança (AuthController)")
public class AuthApiTest extends BaseApiTest {

    @BeforeEach
    public void setup() {
        cleanDatabase();
    }

    @Test
    @DisplayName("POST /api/auth/register - Cadastro com sucesso deve retornar 200 e validar JSON Schema")
    void deveRegistrarUsuarioComSucessoEValidarSchema() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Ana");
        request.setSobrenome("Sustentabilidade");
        request.setEmail("ana.esg@aguabranca.com");
        request.setPassword("senhaForte123");
        request.setRole("GESTOR");
        request.setUnidade("Sede Vitória");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(200)
            .body("message", equalTo("Usuário registrado com sucesso."))
            .body(matchesJsonSchemaInClasspath("schemas/message-response-schema.json"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Cadastro inválido com email duplicado deve retornar 400")
    void deveRejeitarCadastroComEmailDuplicado() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Ana");
        request.setSobrenome("Sustentabilidade");
        request.setEmail("ana.esg@aguabranca.com");
        request.setPassword("senhaForte123");
        request.setRole("GESTOR");
        request.setUnidade("Sede Vitória");

        // Primeiro cadastro
        given().contentType(ContentType.JSON).body(request).post("/api/auth/register").then().statusCode(200);

        // Tentativa duplicada
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/error-response-schema.json"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Autenticação com credenciais válidas deve retornar 200 e token JWT")
    void deveAutenticarUsuarioComSucessoEValidarSchema() {
        String email = "gestor.login@aguabranca.com";
        String password = "senhaCorreta123";
        createAndAuthenticateGestor(email, password);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        loginRequest.setSelectedProfile("GESTOR");

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("email", equalTo(email))
            .body("role", equalTo("GESTOR"))
            .body(matchesJsonSchemaInClasspath("schemas/auth-response-schema.json"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Autenticação com senha errada deve retornar 400 e mensagem de erro")
    void deveRejeitarLoginComSenhaIncorreta() {
        String email = "gestor.login@aguabranca.com";
        createAndAuthenticateGestor(email, "senhaCorreta123");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("senhaIncorreta");
        loginRequest.setSelectedProfile("GESTOR");

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400)
            .body("error", equalTo("Senha inválida."))
            .body(matchesJsonSchemaInClasspath("schemas/error-response-schema.json"));
    }

    @Test
    @DisplayName("GET /api/auth/me - Obter perfil autenticado deve retornar 200 e dados do usuário")
    void deveObterPerfilAutenticadoComSucesso() {
        TestUserAuth auth = createAndAuthenticateGestor("gestor.me@aguabranca.com", "senha123");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + auth.token())
        .when()
            .get("/api/auth/me")
        .then()
            .statusCode(200)
            .body("id", equalTo(auth.usuario().getId()))
            .body("email", equalTo("gestor.me@aguabranca.com"))
            .body("role", equalTo("GESTOR"))
            .body(matchesJsonSchemaInClasspath("schemas/user-profile-schema.json"));
    }

    @Test
    @DisplayName("GET /api/auth/me - Requisição sem token deve retornar 400 Bad Request com mensagem de erro")
    void deveNegarAcessoAoPerfilSemToken() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/auth/me")
        .then()
            .statusCode(400)
            .body("error", equalTo("Usuário não autenticado."))
            .body(matchesJsonSchemaInClasspath("schemas/error-response-schema.json"));
    }
}
