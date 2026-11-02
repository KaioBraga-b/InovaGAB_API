package br.com.inovagab.bdd.steps;

import br.com.inovagab.bdd.CucumberTestContext;
import br.com.inovagab.model.Grupo;
import br.com.inovagab.model.Ideia;
import br.com.inovagab.model.MembroGrupo;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.GrupoRepository;
import br.com.inovagab.repository.IdeiaRepository;
import br.com.inovagab.repository.UsuarioRepository;
import br.com.inovagab.security.JwtTokenProvider;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InovacaoIdeiasSteps {

    @Autowired
    private CucumberTestContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private IdeiaRepository ideiaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Dado("que existe uma Squad ESG configurada e o usuário está autenticado no Squad")
    public void queExisteUmaSquadEsgConfiguradaEOUsuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setNome("Lucas");
        usuario.setSobrenome("Verde");
        usuario.setEmail("lucas.verde@aguabranca.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setRole("OPERADOR");
        usuario = usuarioRepository.save(usuario);

        Grupo grupo = new Grupo();
        grupo.setNome("Squad Coleta Inteligente");
        grupo.setDepartamento("ESG e Logística");
        grupo.setHashId(UUID.randomUUID().toString());
        grupo.setGestorId(usuario.getId());
        grupo.setDataCriacao(LocalDateTime.now());
        grupo.setMembros(new ArrayList<>());
        grupo.getMembros().add(MembroGrupo.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .role(usuario.getRole())
                .dataAdicao(LocalDateTime.now())
                .build());
        grupo = grupoRepository.save(grupo);

        usuario.setGroupId(grupo.getHashId());
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.generateToken(usuario.getId(), usuario.getRole(), grupo.getHashId());
        context.setCurrentUser(usuario);
        context.setCurrentGroup(grupo);
        context.setToken(token);
    }

    @Dado("que eu elaboro uma proposta de ideia com os seguintes dados:")
    public void queEuElaboroUmaPropostaDeIdeia(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Ideia ideia = new Ideia();
        ideia.setTitulo(data.get("titulo"));
        ideia.setDescricao(data.get("descricao"));
        ideia.setArea(data.get("area"));
        ideia.setImpacto(data.get("impacto"));
        ideia.setPrioridade(data.get("prioridade"));

        context.getSessionData().put("currentPayload", ideia);
    }

    @E("a ideia cadastrada deve ter o título {string}")
    public void aIdeiaCadastradaDeveTerOTitulo(String tituloEsperado) {
        context.getResponse().then().body("titulo", equalTo(tituloEsperado));
    }

    @Dado("que existe uma ideia cadastrada com título {string}")
    public void queExisteUmaIdeiaCadastradaComTitulo(String titulo) {
        Ideia ideia = new Ideia();
        ideia.setTitulo(titulo);
        ideia.setDescricao("Descrição detalhada da iniciativa ESG.");
        ideia.setVotos(10);
        ideia.setGroupId(context.getCurrentGroup().getHashId());
        ideia.setUserId(context.getCurrentUser().getId());
        ideia = ideiaRepository.save(ideia);

        context.setCurrentIdea(ideia);
        context.setInitialVotes(10);
    }

    @Quando("eu realizo uma requisição POST de voto para a ideia")
    public void euRealizoUmaRequisicaoPostDeVotoParaAIdeia() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + context.getToken())
                .when()
                .post("/api/inovacao/ideias/" + context.getCurrentIdea().getId() + "/votar");

        context.setResponse(response);
    }

    @E("a quantidade de votos da ideia deve ser incrementada em {int}")
    public void aQuantidadeDeVotosDeveSerIncrementadaEm(int incremento) {
        int expectedVotes = context.getInitialVotes() + incremento;
        context.getResponse().then().body("votos", equalTo(expectedVotes));
    }

    @Dado("que eu tento enviar uma ideia sem título e sem descrição")
    public void queEuTentoEnviarUmaIdeiaSemTituloESemDescricao() {
        Ideia ideiaVazia = new Ideia();
        context.getSessionData().put("ideiaVaziaPayload", ideiaVazia);
    }

    @Quando("eu envio o payload vazio ou nulo para {string}")
    public void euEnvioOPayloadVazioOuNuloPara(String endpoint) {
        Object payload = context.getSessionData().get("ideiaVaziaPayload");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + context.getToken())
                .body(payload != null ? payload : "{}")
                .when()
                .post(endpoint);

        context.setResponse(response);
    }

    @Então("a API deve processar a requisição mantendo a integridade do banco de dados")
    public void aApiDeveProcessarARequisicaoMantendoIntegridade() {
        // Verifica que a resposta é 200 ou 400 mas não derruba a aplicação (500)
        int status = context.getResponse().statusCode();
        assertThat(status, not(equalTo(500)));
    }
}
