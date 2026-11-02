package br.com.inovagab.bdd.steps;

import br.com.inovagab.bdd.CucumberTestContext;
import br.com.inovagab.dto.request.LoginRequest;
import br.com.inovagab.dto.request.RegisterRequest;
import br.com.inovagab.model.Usuario;
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

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AutenticacaoSteps {

    @Autowired
    private CucumberTestContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Dado("que eu envio os dados cadastrais válidos:")
    public void queEuEnvioOsDadosCadastraisValidos(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        RegisterRequest request = new RegisterRequest();
        request.setNome(data.get("nome"));
        request.setSobrenome(data.get("sobrenome"));
        request.setEmail(data.get("email"));
        request.setPassword(data.get("password"));
        request.setRole(data.get("role"));
        request.setUnidade(data.get("unidade"));

        usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> usuarioRepository.deleteById(u.getId()));
        context.getSessionData().put("registerRequest", request);
    }

    @Quando("eu realizo uma requisição POST para {string}")
    public void euRealizoUmaRequisicaoPostPara(String endpoint) {
        Object payload = context.getSessionData().get("registerRequest");
        if (payload == null) {
            payload = context.getSessionData().get("currentPayload");
        }

        var requestSpec = given().contentType(ContentType.JSON);
        if (context.getToken() != null) {
            requestSpec.header("Authorization", "Bearer " + context.getToken());
        }
        if (payload != null) {
            requestSpec.body(payload);
        }

        Response response = requestSpec.when().post(endpoint);
        context.setResponse(response);
    }

    @Dado("que existe um gestor previamente cadastrado com email {string} e senha {string}")
    public void queExisteUmGestorPreviamenteCadastrado(String email, String senha) {
        usuarioRepository.findByEmail(email).ifPresent(u -> usuarioRepository.deleteById(u.getId()));

        Usuario gestor = new Usuario();
        gestor.setNome("Gestor");
        gestor.setSobrenome("Sustentabilidade");
        gestor.setEmail(email);
        gestor.setSenha(passwordEncoder.encode(senha));
        gestor.setRole("GESTOR");
        gestor.setUnidade("Sede Vitória");

        gestor = usuarioRepository.save(gestor);
        context.setCurrentUser(gestor);
    }

    @Quando("eu solicito a autenticação com email {string}, senha {string} e perfil {string}")
    public void euSolicitoAAutenticacao(String email, String senha, String perfil) {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(senha);
        login.setSelectedProfile(perfil);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/auth/login");

        context.setResponse(response);
        if (response.statusCode() == 200) {
            String token = response.jsonPath().getString("token");
            context.setToken(token);
        }
    }

    @E("o corpo da resposta deve conter um token JWT válido")
    public void oCorpoDaRespostaDeveConterUmTokenJwtValido() {
        String token = context.getResponse().jsonPath().getString("token");
        assertThat(token, notNullValue());
        assertThat(jwtTokenProvider.validateToken(token), is(true));
    }

    @E("o perfil retornado deve ser {string}")
    public void oPerfilRetornadoDeveSer(String perfilEsperado) {
        String role = context.getResponse().jsonPath().getString("role");
        assertThat(role, equalTo(perfilEsperado));
    }

    @Dado("que existe um usuário com email {string}")
    public void queExisteUmUsuarioComEmail(String email) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Usuario");
            u.setEmail(email);
            u.setSenha(passwordEncoder.encode("senhaPadrao123"));
            u.setRole("GESTOR");
            usuarioRepository.save(u);
        }
    }

    @Dado("que eu estou autenticado como {string}")
    public void queEuEstouAutenticadoComo(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNome("Auditor");
            u.setEmail(email);
            u.setSenha(passwordEncoder.encode("senha123"));
            u.setRole("GESTOR");
            return usuarioRepository.save(u);
        });

        String token = jwtTokenProvider.generateToken(usuario.getId(), usuario.getRole(), usuario.getGroupId());
        context.setCurrentUser(usuario);
        context.setToken(token);
    }

    @Quando("eu realizo uma requisição GET para {string}")
    public void euRealizoUmaRequisicaoGetPara(String endpoint) {
        var spec = given().contentType(ContentType.JSON);
        if (context.getToken() != null) {
            spec.header("Authorization", "Bearer " + context.getToken());
        }

        Response response = spec.when().get(endpoint);
        context.setResponse(response);
    }

    @E("o email no perfil retornado deve ser {string}")
    public void oEmailNoPerfilRetornadoDeveSer(String emailEsperado) {
        String email = context.getResponse().jsonPath().getString("email");
        assertThat(email, equalTo(emailEsperado));
    }
}
