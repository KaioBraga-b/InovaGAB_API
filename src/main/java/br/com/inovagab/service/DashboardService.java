package br.com.inovagab.service;

import br.com.inovagab.dto.response.DashboardResumoResponse;
import br.com.inovagab.dto.response.ProjetoResponse;
import br.com.inovagab.model.Estrategia;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.repository.EstrategiaRepository;
import br.com.inovagab.repository.IdeiaRepository;
import br.com.inovagab.repository.ProjetoRepository;
import br.com.inovagab.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjetoRepository projetoRepository;
    private final EstrategiaRepository estrategiaRepository;
    private final IdeiaRepository ideiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProjetoService projetoService;

    public DashboardResumoResponse resumoGeral() {
        List<Projeto> projetos = projetoRepository.findAll();

        double investimentoTotal = projetos.stream()
                .mapToDouble(p -> p.getInvestimentoRealizado() > 0
                        ? p.getInvestimentoRealizado() : p.getInvestimento())
                .sum();

        double retornoTotal = projetos.stream()
                .mapToDouble(Projeto::getRetornoObtido)
                .sum();

        double lucroTotal = projetos.stream()
                .mapToDouble(Projeto::getLucroObtido)
                .sum();

        double roiTotal = investimentoTotal > 0
                ? ((retornoTotal - investimentoTotal) / investimentoTotal) * 100
                : 0.0;

        int projetosAtivos = (int) projetos.stream()
                .filter(p -> !"Resultado".equals(p.getStatus()))
                .count();

        double mediaProdutividade = projetos.stream()
                .filter(p -> p.getAumentoProdutividadePercentual() > 0)
                .mapToDouble(Projeto::getAumentoProdutividadePercentual)
                .average()
                .orElse(0.0);

        long ideiasRegistradas = ideiaRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long usuariosComIdeia = ideiaRepository.findAll().stream()
                .map(i -> i.getUserId()).distinct().count();
        double taxaEngajamento = totalUsuarios > 0
                ? ((double) usuariosComIdeia / totalUsuarios) * 100
                : 0.0;

        // Retornos agrupados por estratégia
        List<DashboardResumoResponse.RetornoPorEstrategia> retornosPorEstrategia =
                estrategiaRepository.findAll().stream()
                        .map(e -> buildRetornoPorEstrategia(e, projetos))
                        .collect(Collectors.toList());

        return DashboardResumoResponse.builder()
                .roiTotalPercentual(Math.round(roiTotal * 10.0) / 10.0)
                .lucroObtidoTotal(lucroTotal)
                .investimentoTotal(investimentoTotal)
                .projetosAtivos(projetosAtivos)
                .projetosNoPrazo(projetosAtivos) // simplificado; prazo real requer dataFim
                .ideiasRegistradas(ideiasRegistradas)
                .taxaEngajamento(Math.round(taxaEngajamento * 10.0) / 10.0)
                .aumentoMedioProdutividade(Math.round(mediaProdutividade * 10.0) / 10.0)
                .retornosPorEstrategia(retornosPorEstrategia)
                .build();
    }

    public DashboardResumoResponse resumoPorEstrategia(String estrategiaId) {
        List<Projeto> projetos = projetoRepository.findByEstrategiaId(estrategiaId);

        double investimentoTotal = projetos.stream()
                .mapToDouble(p -> p.getInvestimentoRealizado() > 0
                        ? p.getInvestimentoRealizado() : p.getInvestimento())
                .sum();

        double retornoTotal = projetos.stream().mapToDouble(Projeto::getRetornoObtido).sum();
        double lucroTotal = projetos.stream().mapToDouble(Projeto::getLucroObtido).sum();
        double roi = investimentoTotal > 0
                ? ((retornoTotal - investimentoTotal) / investimentoTotal) * 100 : 0.0;

        long ideias = ideiaRepository.findByEstrategiaId(estrategiaId).size();

        return DashboardResumoResponse.builder()
                .roiTotalPercentual(Math.round(roi * 10.0) / 10.0)
                .lucroObtidoTotal(lucroTotal)
                .investimentoTotal(investimentoTotal)
                .projetosAtivos(projetos.size())
                .ideiasRegistradas(ideias)
                .build();
    }

    public ProjetoResponse detalheProjeto(String projetoId) {
        return projetoService.toResponse(projetoService.findById(projetoId));
    }

    // ---- Helpers ----

    private DashboardResumoResponse.RetornoPorEstrategia buildRetornoPorEstrategia(
            Estrategia e, List<Projeto> todosOsProjetos) {

        List<Projeto> projetosDaEstrategia = todosOsProjetos.stream()
                .filter(p -> e.getId().equals(p.getEstrategiaId()))
                .collect(Collectors.toList());

        double inv = projetosDaEstrategia.stream()
                .mapToDouble(p -> p.getInvestimentoRealizado() > 0
                        ? p.getInvestimentoRealizado() : p.getInvestimento())
                .sum();
        double ret = projetosDaEstrategia.stream()
                .mapToDouble(Projeto::getRetornoObtido).sum();
        double roi = inv > 0 ? ((ret - inv) / inv) * 100 : 0.0;

        return DashboardResumoResponse.RetornoPorEstrategia.builder()
                .estrategiaId(e.getId())
                .estrategiaTitulo(e.getTitulo())
                .totalProjetos(projetosDaEstrategia.size())
                .investimentoTotal(inv)
                .retornoTotal(ret)
                .roi(Math.round(roi * 10.0) / 10.0)
                .build();
    }
}
