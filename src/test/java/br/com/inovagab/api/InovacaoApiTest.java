package br.com.inovagab.api;

import br.com.inovagab.model.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API - Inovação, Projetos, Ideias, Estratégias e Dashboard ESG (InovacaoController)")
public class InovacaoApiTest extends BaseApiTest {

    private TestUserAuth gestorAuth;
    private Grupo grupo;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        gestorAuth = createAndAuthenticateGestor("gestor.inovacao@aguabranca.com", "senha123");
        grupo = createSquadForGestor(gestorAuth.usuario(), "Squad Inovação Sustentável");

        // Re-gera token com groupId
        String tokenComGrupo = jwtTokenProvider.generateToken(
                gestorAuth.usuario().getId(),
                gestorAuth.usuario().getRole(),
                grupo.getHashId()
        );
        gestorAuth = new TestUserAuth(gestorAuth.usuario(), tokenComGrupo);
    }

    // ==========================================
    // PROJETOS ESG
    // ==========================================

    @Test
    @DisplayName("POST /api/inovacao/projetos - Criar projeto ESG com sucesso (200 OK + JSON Schema)")
    void deveCriarProjetoEsgComSucesso() {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Eletrificação de Ônibus Urbanos");
        projeto.setStatus("EM_ANDAMENTO");
        projeto.setArea("Mobilidade Sustentável");
        projeto.setInvestimento("R$ 500.000,00");
        projeto.setLucroObtido(120000.0);
        projeto.setRoi(24.0);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(projeto)
        .when()
            .post("/api/inovacao/projetos")
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Eletrificação de Ônibus Urbanos"))
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/projeto-schema.json"));
    }

    @Test
    @DisplayName("GET /api/inovacao/projetos - Listar projetos ESG da Squad (200 OK + JSON Schema)")
    void deveListarProjetosEsgComSucesso() {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Projeto Energia Solar");
        projeto.setGroupId(grupo.getHashId());
        projetoRepository.save(projeto);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/projetos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/projetos-list-schema.json"));
    }

    @Test
    @DisplayName("PUT /api/inovacao/projetos/{id} - Atualizar projeto ESG (200 OK + JSON Schema)")
    void deveAtualizarProjetoEsgComSucesso() {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Projeto Inicial");
        projeto.setGroupId(grupo.getHashId());
        projeto = projetoRepository.save(projeto);

        projeto.setTitulo("Projeto Inicial Atualizado");
        projeto.setStatus("CONCLUIDO");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(projeto)
        .when()
            .put("/api/inovacao/projetos/" + projeto.getId())
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Projeto Inicial Atualizado"))
            .body(matchesJsonSchemaInClasspath("schemas/projeto-schema.json"));
    }

    @Test
    @DisplayName("DELETE /api/inovacao/projetos/{id} - Excluir projeto ESG (200 OK)")
    void deveExcluirProjetoEsgComSucesso() {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Projeto a Excluir");
        projeto.setGroupId(grupo.getHashId());
        projeto = projetoRepository.save(projeto);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .delete("/api/inovacao/projetos/" + projeto.getId())
        .then()
            .statusCode(200);
    }

    // ==========================================
    // ESTRATÉGIAS ESG
    // ==========================================

    @Test
    @DisplayName("POST /api/inovacao/estrategias - Criar diretriz estratégica ESG (200 OK + JSON Schema)")
    void deveCriarEstrategiaEsgComSucesso() {
        Estrategia estrategia = new Estrategia();
        estrategia.setTitulo("Neutralidade de Carbono 2035");
        estrategia.setDescricao("Metas corporativas para compensação e abatimento de GEE");
        estrategia.setCategoria("Ambiental (E)");
        estrategia.setStatus("ATIVA");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(estrategia)
        .when()
            .post("/api/inovacao/estrategias")
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Neutralidade de Carbono 2035"))
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/estrategia-schema.json"));
    }

    @Test
    @DisplayName("GET /api/inovacao/estrategias - Listar estratégias ESG (200 OK + JSON Schema)")
    void deveListarEstrategiasEsgComSucesso() {
        Estrategia estrategia = new Estrategia();
        estrategia.setTitulo("Gestão de Água e Resíduos");
        estrategia.setGroupId(grupo.getHashId());
        estrategiaRepository.save(estrategia);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/estrategias")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/estrategias-list-schema.json"));
    }

    @Test
    @DisplayName("PUT /api/inovacao/estrategias/{id} - Atualizar estratégia ESG (200 OK + JSON Schema)")
    void deveAtualizarEstrategiaEsgComSucesso() {
        Estrategia estrategia = new Estrategia();
        estrategia.setTitulo("Estratégia Base");
        estrategia.setGroupId(grupo.getHashId());
        estrategia = estrategiaRepository.save(estrategia);

        estrategia.setTitulo("Estratégia Base Expandida");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(estrategia)
        .when()
            .put("/api/inovacao/estrategias/" + estrategia.getId())
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Estratégia Base Expandida"))
            .body(matchesJsonSchemaInClasspath("schemas/estrategia-schema.json"));
    }

    @Test
    @DisplayName("DELETE /api/inovacao/estrategias/{id} - Excluir estratégia ESG (200 OK)")
    void deveExcluirEstrategiaEsgComSucesso() {
        Estrategia estrategia = new Estrategia();
        estrategia.setTitulo("Estratégia Temporária");
        estrategia.setGroupId(grupo.getHashId());
        estrategia = estrategiaRepository.save(estrategia);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .delete("/api/inovacao/estrategias/" + estrategia.getId())
        .then()
            .statusCode(200);
    }

    // ==========================================
    // IDEIAS ESG
    // ==========================================

    @Test
    @DisplayName("POST /api/inovacao/ideias - Cadastrar ideia sustentável (200 OK + JSON Schema)")
    void deveCadastrarIdeiaSustentavelComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Compostagem Orgânica nos Refeitórios");
        ideia.setDescricao("Transformação de resíduos orgânicos em adubo para reflorestamento");
        ideia.setArea("Gestão de Resíduos");
        ideia.setImpacto("ALTO");
        ideia.setPrioridade("MEDIA");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(ideia)
        .when()
            .post("/api/inovacao/ideias")
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Compostagem Orgânica nos Refeitórios"))
            .body("id", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/ideia-schema.json"));
    }

    @Test
    @DisplayName("GET /api/inovacao/ideias - Listar ideias do Squad ESG (200 OK + JSON Schema)")
    void deveListarIdeiasSustentaveisComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Uso Racional de Ar Condicionado");
        ideia.setDescricao("Automação de desligamento nos escritórios");
        ideia.setGroupId(grupo.getHashId());
        ideia.setUserId(gestorAuth.usuario().getId());
        ideiaRepository.save(ideia);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/ideias")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/ideias-list-schema.json"));
    }

    @Test
    @DisplayName("PUT /api/inovacao/ideias/{id} - Atualizar ideia (200 OK + JSON Schema)")
    void deveAtualizarIdeiaComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Ideia Original");
        ideia.setDescricao("Descricao inicial");
        ideia.setGroupId(grupo.getHashId());
        ideia = ideiaRepository.save(ideia);

        ideia.setTitulo("Ideia Aprimorada com Métricas");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(ideia)
        .when()
            .put("/api/inovacao/ideias/" + ideia.getId())
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Ideia Aprimorada com Métricas"))
            .body(matchesJsonSchemaInClasspath("schemas/ideia-schema.json"));
    }

    @Test
    @DisplayName("POST /api/inovacao/ideias/{id}/votar - Votar em ideia (200 OK + JSON Schema)")
    void deveVotarEmIdeiaComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Ideia para Votação");
        ideia.setDescricao("Descricao para votacao");
        ideia.setVotos(5);
        ideia.setGroupId(grupo.getHashId());
        ideia = ideiaRepository.save(ideia);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .post("/api/inovacao/ideias/" + ideia.getId() + "/votar")
        .then()
            .statusCode(200)
            .body("votos", equalTo(6))
            .body(matchesJsonSchemaInClasspath("schemas/ideia-schema.json"));
    }

    @Test
    @DisplayName("POST /api/inovacao/ideias/{id}/comentar - Adicionar comentário (200 OK + JSON Schema)")
    void deveComentarEmIdeiaComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Ideia para Comentário");
        ideia.setDescricao("Descricao");
        ideia.setGroupId(grupo.getHashId());
        ideia = ideiaRepository.save(ideia);

        Comentario comentario = new Comentario();
        comentario.setAutor("Mariana ESG");
        comentario.setTexto("Excelente projeto com alto potencial de ROI ambiental!");

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(comentario)
        .when()
            .post("/api/inovacao/ideias/" + ideia.getId() + "/comentar")
        .then()
            .statusCode(200)
            .body("comentarios", hasSize(1))
            .body(matchesJsonSchemaInClasspath("schemas/ideia-schema.json"));
    }

    @Test
    @DisplayName("DELETE /api/inovacao/ideias/{id} - Excluir ideia (200 OK)")
    void deveExcluirIdeiaComSucesso() {
        Ideia ideia = new Ideia();
        ideia.setTitulo("Ideia a Excluir");
        ideia.setDescricao("Descricao");
        ideia.setGroupId(grupo.getHashId());
        ideia = ideiaRepository.save(ideia);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .delete("/api/inovacao/ideias/" + ideia.getId())
        .then()
            .statusCode(200);
    }

    // ==========================================
    // TRANSAÇÕES FINANCEIRAS (RECEITAS E DESPESAS)
    // ==========================================

    @Test
    @DisplayName("POST /api/inovacao/transacoes - Criar transação financeira (200 OK + JSON Schema)")
    void deveCriarTransacaoFinanceiraComSucesso() {
        TransacaoFinanceira transacao = TransacaoFinanceira.builder()
                .tipo("DESPESA")
                .descricao("Aquisição de lâmpadas LED inteligentes")
                .valor(15000.0)
                .categoria("Eficiência Energética")
                .groupId(grupo.getHashId())
                .build();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(transacao)
        .when()
            .post("/api/inovacao/transacoes")
        .then()
            .statusCode(200)
            .body("tipo", equalTo("DESPESA"))
            .body("valor", equalTo(15000.0f))
            .body(matchesJsonSchemaInClasspath("schemas/transacao-schema.json"));
    }

    @Test
    @DisplayName("GET /api/inovacao/transacoes - Listar transações financeiras (200 OK + JSON Schema)")
    void deveListarTransacoesFinanceirasComSucesso() {
        TransacaoFinanceira transacao = TransacaoFinanceira.builder()
                .tipo("RECEITA")
                .descricao("Venda de créditos de carbono")
                .valor(35000.0)
                .groupId(grupo.getHashId())
                .build();
        transacaoFinanceiraRepository.save(transacao);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/transacoes")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/transacoes-list-schema.json"));
    }

    @Test
    @DisplayName("POST & GET /api/inovacao/receitas - Criar e listar receitas ESG (200 OK + JSON Schema)")
    void deveCriarEListarReceitasComSucesso() {
        TransacaoFinanceira receita = TransacaoFinanceira.builder()
                .tipo("RECEITA")
                .descricao("Economia com reaproveitamento de água pluvial")
                .valor(12000.0)
                .groupId(grupo.getHashId())
                .build();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(receita)
        .when()
            .post("/api/inovacao/receitas")
        .then()
            .statusCode(200)
            .body("tipo", equalTo("RECEITA"))
            .body(matchesJsonSchemaInClasspath("schemas/transacao-schema.json"));

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/receitas")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/transacoes-list-schema.json"));
    }

    @Test
    @DisplayName("POST & GET /api/inovacao/despesas - Criar e listar despesas ESG (200 OK + JSON Schema)")
    void deveCriarEListarDespesasComSucesso() {
        TransacaoFinanceira despesa = TransacaoFinanceira.builder()
                .tipo("DESPESA")
                .descricao("Treinamento de liderança inclusiva")
                .valor(8000.0)
                .groupId(grupo.getHashId())
                .build();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
            .body(despesa)
        .when()
            .post("/api/inovacao/despesas")
        .then()
            .statusCode(200)
            .body("tipo", equalTo("DESPESA"))
            .body(matchesJsonSchemaInClasspath("schemas/transacao-schema.json"));

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/despesas")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body(matchesJsonSchemaInClasspath("schemas/transacoes-list-schema.json"));
    }

    @Test
    @DisplayName("DELETE /api/inovacao/transacoes/{id} - Excluir transação financeira (200 OK)")
    void deveExcluirTransacaoFinanceiraComSucesso() {
        TransacaoFinanceira transacao = TransacaoFinanceira.builder()
                .tipo("DESPESA")
                .descricao("Despesa descartável")
                .valor(100.0)
                .groupId(grupo.getHashId())
                .build();
        transacao = transacaoFinanceiraRepository.save(transacao);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .delete("/api/inovacao/transacoes/" + transacao.getId())
        .then()
            .statusCode(200);
    }

    // ==========================================
    // DASHBOARD RESUMO ESG
    // ==========================================

    @Test
    @DisplayName("GET /api/inovacao/dashboard - Obter Dashboard Resumo de ROI e Sustentabilidade (200 OK + JSON Schema)")
    void deveObterDashboardResumoComSucesso() {
        // Popula projeto com métricas para cálculo de ROI
        Projeto projeto = new Projeto();
        projeto.setTitulo("Usina Solar Central");
        projeto.setStatus("CONCLUIDO");
        projeto.setInvestimento("R$ 200.000,00");
        projeto.setLucroObtido(60000.0);
        projeto.setRoi(30.0);
        projeto.setGroupId(grupo.getHashId());
        projetoRepository.save(projeto);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + gestorAuth.token())
        .when()
            .get("/api/inovacao/dashboard")
        .then()
            .statusCode(200)
            .body("projetosAtivos", notNullValue())
            .body("roiTotalPercentual", notNullValue())
            .body("lucroObtidoTotal", notNullValue())
            .body(matchesJsonSchemaInClasspath("schemas/dashboard-resumo-schema.json"));
    }
}
