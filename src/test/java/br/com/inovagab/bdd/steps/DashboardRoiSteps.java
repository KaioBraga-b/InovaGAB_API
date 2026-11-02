package br.com.inovagab.bdd.steps;

import br.com.inovagab.bdd.CucumberTestContext;
import br.com.inovagab.model.Estrategia;
import br.com.inovagab.model.Grupo;
import br.com.inovagab.model.MembroGrupo;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.model.Usuario;
import br.com.inovagab.repository.EstrategiaRepository;
import br.com.inovagab.repository.GrupoRepository;
import br.com.inovagab.repository.ProjetoRepository;
import br.com.inovagab.repository.UsuarioRepository;
import br.com.inovagab.security.JwtTokenProvider;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DashboardRoiSteps {

    @Autowired
    private CucumberTestContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private EstrategiaRepository estrategiaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Dado("que existe um Squad ESG com projetos e estratégias cadastradas")
    public void queExisteUmSquadEsgComProjetosEEstrategias() {
        Usuario gestor = new Usuario();
        gestor.setNome("Renata");
        gestor.setEmail("renata.esg@aguabranca.com");
        gestor.setSenha(passwordEncoder.encode("senha123"));
        gestor.setRole("GESTOR");
        gestor = usuarioRepository.save(gestor);

        Grupo grupo = new Grupo();
        grupo.setNome("Squad Métricas e ROI Sustentável");
        grupo.setDepartamento("ESG e Governança");
        grupo.setHashId(UUID.randomUUID().toString());
        grupo.setGestorId(gestor.getId());
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

        String token = jwtTokenProvider.generateToken(gestor.getId(), gestor.getRole(), grupo.getHashId());
        context.setCurrentUser(gestor);
        context.setCurrentGroup(grupo);
        context.setToken(token);
    }

    @Dado("que eu defino uma meta estratégica com:")
    public void queEuDefinoUmaMetaEstrategicaCom(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Estrategia estrategia = new Estrategia();
        estrategia.setTitulo(data.get("titulo"));
        estrategia.setDescricao(data.get("descricao"));
        estrategia.setCategoria(data.get("categoria"));
        estrategia.setGroupId(context.getCurrentGroup().getHashId());

        context.getSessionData().put("currentPayload", estrategia);
    }

    @E("a estratégia cadastrada deve conter o título {string}")
    public void aEstrategiaCadastradaDeveConterOTitulo(String tituloEsperado) {
        context.getResponse().then().body("titulo", equalTo(tituloEsperado));
    }

    @Dado("que eu crio um projeto de inovação com os dados:")
    public void queEuCrioUmProjetoDeInovacaoComOsDados(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Projeto projeto = new Projeto();
        projeto.setTitulo(data.get("titulo"));
        projeto.setInvestimento(data.get("investimento"));
        projeto.setStatus(data.get("status"));
        projeto.setArea(data.get("area"));
        projeto.setLucroObtido(Double.parseDouble(data.get("lucroObtido")));
        projeto.setGroupId(context.getCurrentGroup().getHashId());

        context.getSessionData().put("currentPayload", projeto);
    }

    @E("a resposta deve conter indicadores numéricos de ROI e projetos ativos")
    public void aRespostaDeveConterIndicadoresNumericosDeRoiEProjetosAtivos() {
        context.getResponse().then()
                .body("projetosAtivos", notNullValue())
                .body("roiTotalPercentual", notNullValue())
                .body("lucroObtidoTotal", notNullValue())
                .body("investimentoTotal", notNullValue());
    }
}
