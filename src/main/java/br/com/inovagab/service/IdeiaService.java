package br.com.inovagab.service;

import br.com.inovagab.dto.request.IdeiaRequest;
import br.com.inovagab.dto.request.IdeiaStatusRequest;
import br.com.inovagab.dto.response.IdeiaResponse;
import br.com.inovagab.exception.BusinessException;
import br.com.inovagab.exception.ResourceNotFoundException;
import br.com.inovagab.model.Ideia;
import br.com.inovagab.repository.IdeiaRepository;
import br.com.inovagab.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;

    public List<IdeiaResponse> listarTodas(String area, String status) {
        List<Ideia> ideias;
        if (area != null) {
            ideias = ideiaRepository.findByArea(area);
        } else if (status != null) {
            ideias = ideiaRepository.findByStatus(status);
        } else {
            ideias = ideiaRepository.findAll();
        }
        return ideias.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<IdeiaResponse> listarPorUsuario(String userId) {
        return ideiaRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public IdeiaResponse criar(IdeiaRequest request, UserPrincipal principal) {
        Ideia ideia = Ideia.builder()
                .userId(principal.getId())
                .autor(principal.getNome())
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .area(request.getArea())
                .impacto(request.getImpacto())
                .objetivo(request.getObjetivo())
                .prioridade(request.getPrioridade() != null ? request.getPrioridade() : "Média")
                .estrategiaId(request.getEstrategiaId())
                .build();

        return toResponse(ideiaRepository.save(ideia));
    }

    public IdeiaResponse atualizar(String id, IdeiaRequest request, UserPrincipal principal) {
        Ideia ideia = findById(id);

        // Somente o autor ou GESTOR/LIDER pode editar
        boolean isAutor = ideia.getUserId().equals(principal.getId());
        boolean isGestorOuLider = principal.getRole().name().equals("GESTOR")
                || principal.getRole().name().equals("LIDER");

        if (!isAutor && !isGestorOuLider) {
            throw new BusinessException("Você não tem permissão para editar esta ideia.");
        }

        ideia.setTitulo(request.getTitulo());
        ideia.setDescricao(request.getDescricao());
        ideia.setArea(request.getArea());
        if (request.getImpacto() != null) ideia.setImpacto(request.getImpacto());
        if (request.getObjetivo() != null) ideia.setObjetivo(request.getObjetivo());
        if (request.getPrioridade() != null) ideia.setPrioridade(request.getPrioridade());
        if (request.getEstrategiaId() != null) ideia.setEstrategiaId(request.getEstrategiaId());

        return toResponse(ideiaRepository.save(ideia));
    }

    public IdeiaResponse atualizarStatus(String id, IdeiaStatusRequest request) {
        Ideia ideia = findById(id);

        ideia.setStatus(request.getStatus());
        ideia.setJustificativa(request.getJustificativa());

        // Transições de estado automáticas
        switch (request.getStatus()) {
            case "Em análise" -> {
                ideia.setEtapa("Em análise pelo gestor");
                ideia.setProgresso(0.33);
            }
            case "Aprovada" -> {
                ideia.setEtapa("Aprovada pelo gestor");
                ideia.setProgresso(1.0);
            }
            case "Recusada" -> {
                ideia.setEtapa("Recusada");
                ideia.setProgresso(0.0);
            }
            default -> { /* status customizado */ }
        }

        return toResponse(ideiaRepository.save(ideia));
    }

    public IdeiaResponse votar(String id) {
        Ideia ideia = findById(id);
        ideia.setVotos(ideia.getVotos() + 1);
        return toResponse(ideiaRepository.save(ideia));
    }

    public void deletar(String id, UserPrincipal principal) {
        Ideia ideia = findById(id);

        boolean isAutor = ideia.getUserId().equals(principal.getId());
        boolean isGestorOuLider = principal.getRole().name().equals("GESTOR")
                || principal.getRole().name().equals("LIDER");

        if (!isAutor && !isGestorOuLider) {
            throw new BusinessException("Você não tem permissão para excluir esta ideia.");
        }

        ideiaRepository.delete(ideia);
    }

    // ---- Helpers ----

    private Ideia findById(String id) {
        return ideiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ideia", "id", id));
    }

    private IdeiaResponse toResponse(Ideia i) {
        return IdeiaResponse.builder()
                .id(i.getId())
                .userId(i.getUserId())
                .autor(i.getAutor())
                .titulo(i.getTitulo())
                .descricao(i.getDescricao())
                .area(i.getArea())
                .impacto(i.getImpacto())
                .objetivo(i.getObjetivo())
                .prioridade(i.getPrioridade())
                .status(i.getStatus())
                .etapa(i.getEtapa())
                .progresso(i.getProgresso())
                .votos(i.getVotos())
                .estrategiaId(i.getEstrategiaId())
                .justificativa(i.getJustificativa())
                .dataCriacao(i.getDataCriacao())
                .dataAtualizacao(i.getDataAtualizacao())
                .build();
    }
}
