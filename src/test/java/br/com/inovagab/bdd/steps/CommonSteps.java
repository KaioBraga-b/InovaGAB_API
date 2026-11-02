package br.com.inovagab.bdd.steps;

import br.com.inovagab.bdd.CucumberTestContext;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CommonSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private CucumberTestContext context;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        context.reset();
    }

    @Dado("que a API de autenticação está disponível")
    public void queApiDeAutenticacaoEstaDisponivel() {
        // API está inicializada pelo Spring Boot no port configurado
        assertThat(port, greaterThan(0));
    }

    @Então("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int statusCode) {
        assertThat(context.getResponse(), notNullValue());
        context.getResponse().then().statusCode(statusCode);
    }

    @E("a resposta deve respeitar o contrato JSON Schema {string}")
    public void aRespostaDeveRespeitarOContratoJsonSchema(String schemaPath) {
        assertThat(context.getResponse(), notNullValue());
        context.getResponse().then().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    @E("o corpo da resposta deve conter a mensagem {string}")
    public void oCorpoDaRespostaDeveConterAMensagem(String mensagemEsperada) {
        assertThat(context.getResponse(), notNullValue());
        context.getResponse().then().body("message", containsString(mensagemEsperada));
    }

    @E("o corpo da resposta deve conter o erro {string}")
    public void oCorpoDaRespostaDeveConterOErro(String erroEsperado) {
        assertThat(context.getResponse(), notNullValue());
        context.getResponse().then().body("error", containsString(erroEsperado));
    }
}
