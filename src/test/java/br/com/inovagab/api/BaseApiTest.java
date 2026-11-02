package br.com.inovagab.api;

import br.com.inovagab.model.Grupo;
import br.com.inovagab.model.MembroGrupo;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.*;
import br.com.inovagab.security.JwtTokenProvider;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected GrupoRepository grupoRepository;

    @Autowired
    protected ProjetoRepository projetoRepository;

    @Autowired
    protected IdeiaRepository ideiaRepository;

    @Autowired
    protected EstrategiaRepository estrategiaRepository;

    @Autowired
    protected TransacaoFinanceiraRepository transacaoFinanceiraRepository;

    @Autowired
    protected NotificacaoRepository notificacaoRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public void cleanDatabase() {
        ideiaRepository.deleteAll();
        projetoRepository.deleteAll();
        estrategiaRepository.deleteAll();
        transacaoFinanceiraRepository.deleteAll();
        notificacaoRepository.deleteAll();
        grupoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    public record TestUserAuth(Usuario usuario, String token) {}

    public TestUserAuth createAndAuthenticateGestor(String email, String rawPassword) {
        return createAndAuthenticateUser(email, rawPassword, "GESTOR", null);
    }

    public TestUserAuth createAndAuthenticateColaborador(String email, String rawPassword) {
        return createAndAuthenticateUser(email, rawPassword, "OPERADOR", null);
    }

    public TestUserAuth createAndAuthenticateUser(String email, String rawPassword, String role, String groupId) {
        usuarioRepository.findByEmail(email).ifPresent(u -> usuarioRepository.deleteById(u.getId()));

        Usuario usuario = new Usuario();
        usuario.setNome("Test");
        usuario.setSobrenome("User");
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(rawPassword));
        usuario.setRole(role);
        usuario.setUnidade("Unidade Teste ESG");
        usuario.setGroupId(groupId);

        usuario = usuarioRepository.save(usuario);
        String token = jwtTokenProvider.generateToken(usuario.getId(), usuario.getRole(), usuario.getGroupId());
        return new TestUserAuth(usuario, token);
    }

    public Grupo createSquadForGestor(Usuario gestor, String squadName) {
        Grupo grupo = new Grupo();
        grupo.setNome(squadName);
        grupo.setDepartamento("Sustentabilidade Corporativa");
        grupo.setDescricao("Squad focada em metas ESG e ecoeficiência");
        grupo.setGestorId(gestor.getId());
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

        gestor.setGroupId(grupo.getHashId());
        usuarioRepository.save(gestor);

        return grupo;
    }
}
