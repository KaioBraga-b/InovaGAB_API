package br.com.inovagab.service;

import br.com.inovagab.dto.request.EstrategiaRequest;
import br.com.inovagab.dto.response.EstrategiaResponse;
import br.com.inovagab.dto.response.HistoricoEstrategiaResponse;
import br.com.inovagab.exception.ResourceNotFoundException;
import br.com.inovagab.model.Estrategia;
import br.com.inovagab.model.HistoricoEstrategia;
import br.com.inovagab.repository.EstrategiaRepository;
import br.com.inovagab.repository.HistoricoEstrategiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstrategiaService {

    private final EstrategiaRepository estrategiaRepository;
    private final HistoricoEstrategiaRepository historicoRepository;

    public List<EstrategiaResponse> listarTodas() {
        return estrategiaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EstrategiaResponse criar(EstrategiaRequest request, br.com.inovagab.security.UserPrincipal principal) {
        Estrategia estrategia = Estrategia.builder()
                .criadoPorId(principal.getId())
                .criadoPorNome(principal.getNome())
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .categoria(request.getCategoria())
                .campanha(request.getCampanha())
                .etapa(request.getEtapa() != null ? request.getEtapa() : "Planejamento")
                .progresso(request.getProgresso())
                .build();

        return toResponse(estrategiaRepository.save(estrategia));
    }

    public EstrategiaResponse atualizar(String id, EstrategiaRequest request) {
        Estrategia estrategia = findById(id);

        estrategia.setTitulo(request.getTitulo());
        estrategia.setDescricao(request.getDescricao());
        estrategia.setCategoria(request.getCategoria());
        estrategia.setCampanha(request.getCampanha());
        if (request.getEtapa() != null)
            estrategia.setEtapa(request.getEtapa());
        estrategia.setProgresso(request.getProgresso());

        // Ao concluir, gerar histórico imutável
        if ("Concluído".equals(request.getEtapa())) {
            estrategia.setAtiva(false);
            registrarHistorico(estrategia);
        }

        return toResponse(estrategiaRepository.save(estrategia));
    }

    public void deletar(String id) {
        Estrategia estrategia = findById(id);
        estrategiaRepository.delete(estrategia);
    }

    public List<HistoricoEstrategiaResponse> listarHistorico() {
        return historicoRepository.findAll()
                .stream()
                .map(this::toHistoricoResponse)
                .collect(Collectors.toList());
    }

    // ---- Helpers ----

    private Estrategia findById(String id) {
        return estrategiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estratégia", "id", id));
    }

    private void registrarHistorico(Estrategia e) {
        HistoricoEstrategia historico = HistoricoEstrategia.builder()
                .estrategiaId(e.getId())
                .dataRegistro(Instant.now())
                .categoria(e.getCategoria())
                .campanha(e.getCampanha())
                .resultadoFinal(e.getEtapa())
                .build();
        historicoRepository.save(historico);
    }

    private EstrategiaResponse toResponse(Estrategia e) {
        return EstrategiaResponse.builder()
                .id(e.getId())
                .titulo(e.getTitulo())
                .descricao(e.getDescricao())
                .categoria(e.getCategoria())
                .campanha(e.getCampanha())
                .etapa(e.getEtapa())
                .progresso(e.getProgresso())
                .ativa(e.isAtiva())
                .dataCriacao(e.getDataCriacao())
                .dataAtualizacao(e.getDataAtualizacao())
                .build();
    }

    private HistoricoEstrategiaResponse toHistoricoResponse(HistoricoEstrategia h) {
        return HistoricoEstrategiaResponse.builder()
                .id(h.getId())
                .estrategiaId(h.getEstrategiaId())
                .dataRegistro(h.getDataRegistro())
                .categoria(h.getCategoria())
                .campanha(h.getCampanha())
                .resultadoFinal(h.getResultadoFinal())
                .roiAlcancado(h.getRoiAlcancado())
                .build();
    }
}
