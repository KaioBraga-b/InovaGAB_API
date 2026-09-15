package br.com.inovagab.service;

import br.com.inovagab.model.Estrategia;
import br.com.inovagab.model.Ideia;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.model.Notificacao;
import br.com.inovagab.model.Comentario;
import br.com.inovagab.repository.EstrategiaRepository;
import br.com.inovagab.repository.IdeiaRepository;
import br.com.inovagab.repository.ProjetoRepository;
import br.com.inovagab.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InovacaoService {

    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private EstrategiaRepository estrategiaRepository;
    
    @Autowired
    private IdeiaRepository ideiaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public List<Projeto> getAllProjetos() {
        return projetoRepository.findAll();
    }

    public Projeto addProjeto(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    public Projeto updateProjeto(String id, Projeto projetoUpdates) {
        return projetoRepository.findById(id).map(p -> {
            if(projetoUpdates.getTitulo() != null) p.setTitulo(projetoUpdates.getTitulo());
            if(projetoUpdates.getArea() != null) p.setArea(projetoUpdates.getArea());
            if(projetoUpdates.getProgresso() != null) p.setProgresso(projetoUpdates.getProgresso());
            if(projetoUpdates.getEtapaAtiva() != null) p.setEtapaAtiva(projetoUpdates.getEtapaAtiva());
            if(projetoUpdates.getStatus() != null) p.setStatus(projetoUpdates.getStatus());
            if(projetoUpdates.getProgressoTexto() != null) p.setProgressoTexto(projetoUpdates.getProgressoTexto());
            if(projetoUpdates.getStatusColor() != null) p.setStatusColor(projetoUpdates.getStatusColor());
            if(projetoUpdates.getStatusBg() != null) p.setStatusBg(projetoUpdates.getStatusBg());
            
            if(projetoUpdates.getResultadoValor() != null) p.setResultadoValor(projetoUpdates.getResultadoValor());
            if(projetoUpdates.getResultadoRoi() != null) p.setResultadoRoi(projetoUpdates.getResultadoRoi());
            if(projetoUpdates.getInvestimento() != null) p.setInvestimento(projetoUpdates.getInvestimento());
            if(projetoUpdates.getEstrategiaId() != null) p.setEstrategiaId(projetoUpdates.getEstrategiaId());
            if(projetoUpdates.getEstrategiaTitulo() != null) p.setEstrategiaTitulo(projetoUpdates.getEstrategiaTitulo());
            if(projetoUpdates.getLucroObtido() != null) p.setLucroObtido(projetoUpdates.getLucroObtido());
            if(projetoUpdates.getAumentoProdutividade() != null) p.setAumentoProdutividade(projetoUpdates.getAumentoProdutividade());
            if(projetoUpdates.getNoPrazo() != null) p.setNoPrazo(projetoUpdates.getNoPrazo());
            if(projetoUpdates.getDataInicio() != null) p.setDataInicio(projetoUpdates.getDataInicio());
            if(projetoUpdates.getPrazo() != null) p.setPrazo(projetoUpdates.getPrazo());
            if(projetoUpdates.getTarefas() != null) p.setTarefas(projetoUpdates.getTarefas());

            return projetoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Projeto no encontrado"));
    }

    public void deleteProjeto(String id) {
        projetoRepository.deleteById(id);
    }

    // Estrategia
    public List<Estrategia> getAllEstrategias() {
        return estrategiaRepository.findAll();
    }

    public Estrategia addEstrategia(Estrategia estrategia) {
        return estrategiaRepository.save(estrategia);
    }

    public Estrategia updateEstrategia(String id, Estrategia estrategiaUpdates) {
        return estrategiaRepository.findById(id).map(e -> {
            if(estrategiaUpdates.getTitulo() != null) e.setTitulo(estrategiaUpdates.getTitulo());
            if(estrategiaUpdates.getDescricao() != null) e.setDescricao(estrategiaUpdates.getDescricao());
            if(estrategiaUpdates.getProgresso() != null) e.setProgresso(estrategiaUpdates.getProgresso());
            if(estrategiaUpdates.getStatus() != null) e.setStatus(estrategiaUpdates.getStatus());
            if(estrategiaUpdates.getStatusColor() != null) e.setStatusColor(estrategiaUpdates.getStatusColor());
            if(estrategiaUpdates.getStatusTextColor() != null) e.setStatusTextColor(estrategiaUpdates.getStatusTextColor());
            
            if(estrategiaUpdates.getCategoria() != null) e.setCategoria(estrategiaUpdates.getCategoria());
            if(estrategiaUpdates.getCampanha() != null) e.setCampanha(estrategiaUpdates.getCampanha());

            return estrategiaRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Estrategia no encontrada"));
    }

    public void deleteEstrategia(String id) {
        estrategiaRepository.deleteById(id);
    }

    // Ideia
    public List<Ideia> getAllIdeias() {
        return ideiaRepository.findAll();
    }

    public Ideia addIdeia(Ideia ideia) {
        Ideia savedIdeia = ideiaRepository.save(ideia);
        
        Notificacao notifAll = new Notificacao();
        notifAll.setMensagem("Nova ideia registrada: " + savedIdeia.getTitulo());
        notifAll.setDestinatarioRole("TODOS");
        notifAll.setTipo("NOVA_IDEIA");
        notifAll.setLida(false);
        notifAll.setIdeiaId(savedIdeia.getId());
        notifAll.setDataCriacao(java.time.LocalDateTime.now());
        notificacaoRepository.save(notifAll);

        return savedIdeia;
    }

    public Ideia updateIdeia(String id, Ideia ideiaUpdates) {
        return ideiaRepository.findById(id).map(i -> {
            if(ideiaUpdates.getTitulo() != null) i.setTitulo(ideiaUpdates.getTitulo());
            if(ideiaUpdates.getDescricao() != null) i.setDescricao(ideiaUpdates.getDescricao());
            if(ideiaUpdates.getArea() != null) i.setArea(ideiaUpdates.getArea());
            if(ideiaUpdates.getStatus() != null) i.setStatus(ideiaUpdates.getStatus());
            if(ideiaUpdates.getStatusColor() != null) i.setStatusColor(ideiaUpdates.getStatusColor());
            if(ideiaUpdates.getStatusTextColor() != null) i.setStatusTextColor(ideiaUpdates.getStatusTextColor());
            if(ideiaUpdates.getEtapa() != null) i.setEtapa(ideiaUpdates.getEtapa());
            if(ideiaUpdates.getProgresso() != null) i.setProgresso(ideiaUpdates.getProgresso());
            return ideiaRepository.save(i);
        }).orElseThrow(() -> new RuntimeException("Ideia no encontrada"));
    }

    public void deleteIdeia(String id) {
        ideiaRepository.deleteById(id);
    }

    public Ideia votarIdeia(String id) {
        return ideiaRepository.findById(id).map(i -> {
            i.setVotos(i.getVotos() != null ? i.getVotos() + 1 : 1);
            return ideiaRepository.save(i);
        }).orElseThrow(() -> new RuntimeException("Ideia não encontrada"));
    }

    public Ideia comentarIdeia(String id, Comentario comentario) {
        return ideiaRepository.findById(id).map(i -> {
            if (i.getComentarios() == null) i.setComentarios(new java.util.ArrayList<>());
            if (comentario.getDataHora() == null) comentario.setDataHora(java.time.LocalDateTime.now());
            i.getComentarios().add(comentario);
            return ideiaRepository.save(i);
        }).orElseThrow(() -> new RuntimeException("Ideia não encontrada"));
    }

    public br.com.inovagab.dto.response.DashboardResumoResponse getDashboardResumo() {
        List<Projeto> projetos = projetoRepository.findAll();
        long ideiasRegistradas = ideiaRepository.count();
        
        double lucroTotal = 0.0;
        double investimentoTotal = 0.0;
        int projetosAtivos = 0;
        int projetosNoPrazo = 0;
        double somaProdutividade = 0.0;

        java.util.Map<String, br.com.inovagab.dto.response.DashboardResumoResponse.RetornoPorEstrategia> mapaRetornos = new java.util.HashMap<>();

        for (Projeto p : projetos) {
            if (!"Concludo".equalsIgnoreCase(p.getStatus())) {
                projetosAtivos++;
            }
            if (p.getNoPrazo() != null && p.getNoPrazo()) {
                projetosNoPrazo++;
            }
            if (p.getLucroObtido() != null) {
                lucroTotal += p.getLucroObtido();
            }
            if (p.getAumentoProdutividade() != null) {
                somaProdutividade += p.getAumentoProdutividade();
            }
            
            double invProj = 0.0;
            if (p.getInvestimento() != null) {
                try {
                    invProj = Double.parseDouble(p.getInvestimento().replaceAll("[^\\d.]", ""));
                } catch (Exception e) {}
                investimentoTotal += invProj;
            }

            if (p.getEstrategiaId() != null && !p.getEstrategiaId().isEmpty()) {
                br.com.inovagab.dto.response.DashboardResumoResponse.RetornoPorEstrategia ret = mapaRetornos.getOrDefault(p.getEstrategiaId(), 
                    new br.com.inovagab.dto.response.DashboardResumoResponse.RetornoPorEstrategia(p.getEstrategiaId(), p.getEstrategiaTitulo() != null ? p.getEstrategiaTitulo() : "Sem Ttulo", 0, 0.0, 0.0, 0.0));
                
                ret.setTotalProjetos(ret.getTotalProjetos() + 1);
                ret.setInvestimentoTotal(ret.getInvestimentoTotal() + invProj);
                if (p.getLucroObtido() != null) {
                    ret.setRetornoTotal(ret.getRetornoTotal() + p.getLucroObtido());
                }
                if (ret.getInvestimentoTotal() > 0) {
                    ret.setRoi(((ret.getRetornoTotal() - ret.getInvestimentoTotal()) / ret.getInvestimentoTotal()) * 100);
                }
                mapaRetornos.put(p.getEstrategiaId(), ret);
            }
        }

        double roiTotal = 0.0;
        if (investimentoTotal > 0) {
            roiTotal = ((lucroTotal - investimentoTotal) / investimentoTotal) * 100;
        }

        double aumentoMedio = projetos.size() > 0 ? somaProdutividade / projetos.size() : 0.0;
        double taxaEngajamento = projetosAtivos > 0 ? ((double) ideiasRegistradas / projetosAtivos) * 10 : 0.0;

        return br.com.inovagab.dto.response.DashboardResumoResponse.builder()
                .roiTotalPercentual(roiTotal)
                .lucroObtidoTotal(lucroTotal)
                .investimentoTotal(investimentoTotal)
                .projetosAtivos(projetosAtivos)
                .projetosNoPrazo(projetosNoPrazo)
                .ideiasRegistradas(ideiasRegistradas)
                .taxaEngajamento(taxaEngajamento)
                .aumentoMedioProdutividade(aumentoMedio)
                .retornosPorEstrategia(new java.util.ArrayList<>(mapaRetornos.values()))
                .build();
    }
}