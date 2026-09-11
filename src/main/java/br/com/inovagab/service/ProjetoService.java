package br.com.inovagab.service;

import br.com.inovagab.dto.request.ProjetoRequest;
import br.com.inovagab.dto.response.ProjetoResponse;
import br.com.inovagab.exception.ResourceNotFoundException;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    public List<ProjetoResponse> listarTodos() {
        return projetoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProjetoResponse criar(ProjetoRequest request, br.com.inovagab.security.UserPrincipal principal) {
        Projeto projeto = Projeto.builder()
                .criadoPorId(principal.getId())
                .criadoPorNome(principal.getNome())
                .titulo(request.getTitulo())
                .area(request.getArea())
                .etapaAtiva(request.getEtapaAtiva())
                .progresso(request.getProgresso())
                .periodo(request.getPeriodo())
                .investimento(request.getInvestimento())
                .retornoMensalEstimado(request.getRetornoMensalEstimado())
                .prazoMeses(request.getPrazoMeses())
                .estrategiaId(request.getEstrategiaId())
                .status(resolverStatus(request.getEtapaAtiva()))
                .build();

        return toResponse(projetoRepository.save(projeto));
    }

    public ProjetoResponse atualizar(String id, ProjetoRequest request) {
        Projeto projeto = findById(id);

        projeto.setTitulo(request.getTitulo());
        projeto.setArea(request.getArea());
        projeto.setEtapaAtiva(request.getEtapaAtiva());
        projeto.setProgresso(request.getProgresso());
        projeto.setStatus(resolverStatus(request.getEtapaAtiva()));
        if (request.getPeriodo() != null)
            projeto.setPeriodo(request.getPeriodo());
        if (request.getInvestimento() > 0)
            projeto.setInvestimento(request.getInvestimento());
        if (request.getInvestimentoRealizado() > 0)
            projeto.setInvestimentoRealizado(request.getInvestimentoRealizado());
        if (request.getRetornoMensalEstimado() > 0)
            projeto.setRetornoMensalEstimado(request.getRetornoMensalEstimado());
        if (request.getRetornoObtido() > 0) {
            projeto.setRetornoObtido(request.getRetornoObtido());
            // Cálculo automático de lucro e ROI
            double investRef = projeto.getInvestimentoRealizado() > 0
                    ? projeto.getInvestimentoRealizado()
                    : projeto.getInvestimento();
            projeto.setLucroObtido(request.getRetornoObtido() - investRef);
            if (investRef > 0) {
                projeto.setRoiPercentual(
                        ((request.getRetornoObtido() - investRef) / investRef) * 100);
            }
        }
        if (request.getAumentoProdutividade() > 0)
            projeto.setAumentoProdutividadePercentual(request.getAumentoProdutividade());
        if (request.getPrazoMeses() > 0)
            projeto.setPrazoMeses(request.getPrazoMeses());
        if (request.getEstrategiaId() != null)
            projeto.setEstrategiaId(request.getEstrategiaId());

        return toResponse(projetoRepository.save(projeto));
    }

    public void deletar(String id) {
        Projeto projeto = findById(id);
        projetoRepository.delete(projeto);
    }

    // ---- Helpers ----

    private String resolverStatus(int etapaAtiva) {
        return switch (etapaAtiva) {
            case 0 -> "Ideação";
            case 1 -> "Aprovação";
            case 2 -> "Execução";
            case 3 -> "Resultado";
            default -> "Ideação";
        };
    }

    public Projeto findById(String id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto", "id", id));
    }

    public ProjetoResponse toResponse(Projeto p) {
        return ProjetoResponse.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .area(p.getArea())
                .status(p.getStatus())
                .etapaAtiva(p.getEtapaAtiva())
                .progresso(p.getProgresso())
                .periodo(p.getPeriodo())
                .investimento(p.getInvestimento())
                .investimentoRealizado(p.getInvestimentoRealizado())
                .retornoMensalEstimado(p.getRetornoMensalEstimado())
                .retornoObtido(p.getRetornoObtido())
                .lucroObtido(p.getLucroObtido())
                .roiPercentual(p.getRoiPercentual())
                .aumentoProdutividadePercentual(p.getAumentoProdutividadePercentual())
                .prazoMeses(p.getPrazoMeses())
                .estrategiaId(p.getEstrategiaId())
                .dataInicio(p.getDataInicio())
                .dataAtualizacao(p.getDataAtualizacao())
                .build();
    }
}
