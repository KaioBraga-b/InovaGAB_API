package br.com.inovagab.bdd.steps;

import br.com.inovagab.bdd.CucumberTestContext;
import br.com.inovagab.dto.request.GrupoRequest;
import br.com.inovagab.dto.request.MembroRequest;
import br.com.inovagab.model.Grupo;
import br.com.inovagab.model.MembroGrupo;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.GrupoRepository;
import br.com.inovagab.repository.UsuarioRepository;
import br.com.inovagab.security.JwtTokenProvider;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
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

public class GestaoSquadsSteps {

    @Autowired
    private CucumberTestContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Dado("que eu estou autenticado como GESTOR com email {string}")
    public void queEuEstouAutenticadoComoGestorComEmail(String email) {
        usuarioRepository.findByEmail(email).ifPresent(u -> usuarioRepository.deleteById(u.getId()));

        Usuario gestor = new Usuario();
        gestor.setNome("Gestor");
        gestor.setEmail(email);
        gestor.setSenha(passwordEncoder.encode("senha123"));
        gestor.setRole("GESTOR");
        gestor = usuarioRepository.save(gestor);

        String token = jwtTokenProvider.generateToken(gestor.getId(), "GESTOR", null);
        context.setCurrentUser(gestor);
        context.setToken(token);
    }

    @Dado("que eu preparo a criação de uma Squad com os dados:")
    public void queEuPreparoACriacaoDeUmaSquad(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        GrupoRequest request = new GrupoRequest();
        request.setNome(data.get("nome"));
        request.setDepartamento(data.get("departamento"));
        request.setDescricao(data.get("descricao"));

        context.getSessionData().put("grupoRequest", request);
    }

    @Quando("eu envio uma requisição POST para {string}")
    public void euEnvioUmaRequisicaoPostPara(String endpoint) {
        Object payload = context.getSessionData().get("grupoRequest");
        if (payload == null) {
            payload = context.getSessionData().get("currentPayload");
        }

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + context.getToken())
                .body(payload)
                .when()
                .post(endpoint);

        context.setResponse(response);
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            String hashId = response.jsonPath().getString("hashId");
            if (hashId != null) {
                grupoRepository.findByHashId(hashId).ifPresent(context::setCurrentGroup);
            }
        }
    }

    @E("a squad criada deve ter o nome {string}")
    public void aSquadCriadaDeveTerONome(String nomeEsperado) {
        String nome = context.getResponse().jsonPath().getString("nome");
        assertThat(nome, equalTo(nomeEsperado));
    }

    @Dado("que existe uma Squad de inovação ESG criada pelo gestor")
    public void queExisteUmaSquadCriadaPeloGestor() {
        Usuario gestor = context.getCurrentUser();
        Grupo grupo = new Grupo();
        grupo.setNome("Squad Sustentabilidade Integrada");
        grupo.setDepartamento("ESG");
        grupo.setDescricao("Squad ESG corporativa");
        grupo.setGestorId(gestor.getId());
        grupo.setGestorEmail(gestor.getEmail());
        grupo.setHashId(UUID.randomUUID().toString());
        grupo.setDataCriacao(LocalDateTime.now());
        grupo.setMembros(new ArrayList<>());
        grupo.getMembros().add(MembroGrupo.builder()
                .usuarioId(gestor.getId())
                .email(gestor.getEmail())
                .nome(gestor.getNome())
                .role(gestor.getRole())
                .dataAdicao(LocalDateTime.now())
                .build());

        grupo = grupoRepository.save(grupo);
        context.setCurrentGroup(grupo);
    }

    @Quando("o gestor adiciona o membro com email {string}, nome {string} e role {string}")
    public void oGestorAdicionaOMembro(String email, String nome, String role) {
        // Assegura que o usuário existe para associar
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario novoMembro = new Usuario();
            novoMembro.setNome(nome);
            novoMembro.setEmail(email);
            novoMembro.setSenha(passwordEncoder.encode("senha123"));
            novoMembro.setRole("OPERADOR");
            usuarioRepository.save(novoMembro);
        }

        MembroRequest request = new MembroRequest();
        request.setEmail(email);
        request.setRole("OPERADOR"); // Mapeia para enum Role.OPERADOR

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + context.getToken())
                .body(request)
                .when()
                .post("/api/gestor/grupos/" + context.getCurrentGroup().getHashId() + "/membros");

        context.setResponse(response);
    }

    @E("a lista de membros da squad deve conter o email {string}")
    public void aListaDeMembrosDeveConterOEmail(String emailEsperado) {
        context.getResponse().then().body("membros.email", hasItem(emailEsperado));
    }

    @Dado("que eu estou autenticado como COLABORADOR com email {string}")
    public void queEuEstouAutenticadoComoColaborador(String email) {
        usuarioRepository.findByEmail(email).ifPresent(u -> usuarioRepository.deleteById(u.getId()));

        Usuario colab = new Usuario();
        colab.setNome("Colaborador");
        colab.setEmail(email);
        colab.setSenha(passwordEncoder.encode("senha123"));
        colab.setRole("OPERADOR");
        colab = usuarioRepository.save(colab);

        String token = jwtTokenProvider.generateToken(colab.getId(), "OPERADOR", null);
        context.setCurrentUser(colab);
        context.setToken(token);
    }

    @Quando("eu tento criar uma squad com nome {string}")
    public void euTentoCriarUmaSquadComNome(String nome) {
        GrupoRequest request = new GrupoRequest();
        request.setNome(nome);
        request.setDepartamento("TI");
        request.setDescricao("Tentativa não autorizada");

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + context.getToken())
                .body(request)
                .when()
                .post("/api/gestor/grupos");

        context.setResponse(response);
    }

    @E("a mensagem de erro deve indicar que o acesso é exclusivo para perfil GESTOR")
    public void aMensagemDeErroDeveIndicarAcessoExclusivo() {
        context.getResponse().then().body("error", containsString("exclusivo para usuários com perfil GESTOR"));
    }
}
