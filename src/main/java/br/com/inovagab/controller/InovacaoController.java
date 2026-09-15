package br.com.inovagab.controller;

import br.com.inovagab.model.Estrategia;
import br.com.inovagab.model.Ideia;
import br.com.inovagab.model.Projeto;
import br.com.inovagab.model.Comentario;
import br.com.inovagab.model.TransacaoFinanceira;
import br.com.inovagab.service.InovacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inovacao")
public class InovacaoController {

    @Autowired
    private InovacaoService inovacaoService;

    // Projetos
    @GetMapping("/projetos")
    public ResponseEntity<List<Projeto>> getAllProjetos() {
        return ResponseEntity.ok(inovacaoService.getAllProjetos());
    }

    @PostMapping("/projetos")
    public ResponseEntity<Projeto> addProjeto(@RequestBody Projeto projeto) {
        if (projeto.getId() != null && projeto.getId().isEmpty()) projeto.setId(null);
        return ResponseEntity.ok(inovacaoService.addProjeto(projeto));
    }

    @PutMapping("/projetos/{id}")
    public ResponseEntity<Projeto> updateProjeto(@PathVariable String id, @RequestBody Projeto projeto) {
        return ResponseEntity.ok(inovacaoService.updateProjeto(id, projeto));
    }

    @DeleteMapping("/projetos/{id}")
    public ResponseEntity<Void> deleteProjeto(@PathVariable String id) {
        inovacaoService.deleteProjeto(id);
        return ResponseEntity.ok().build();
    }

    // Estrategias
    @GetMapping("/estrategias")
    public ResponseEntity<List<Estrategia>> getAllEstrategias() {
        return ResponseEntity.ok(inovacaoService.getAllEstrategias());
    }

    @PostMapping("/estrategias")
    public ResponseEntity<Estrategia> addEstrategia(@RequestBody Estrategia estrategia) {
        if (estrategia.getId() != null && estrategia.getId().isEmpty()) estrategia.setId(null);
        return ResponseEntity.ok(inovacaoService.addEstrategia(estrategia));
    }

    @PutMapping("/estrategias/{id}")
    public ResponseEntity<Estrategia> updateEstrategia(@PathVariable String id, @RequestBody Estrategia estrategia) {
        return ResponseEntity.ok(inovacaoService.updateEstrategia(id, estrategia));
    }

    @DeleteMapping("/estrategias/{id}")
    public ResponseEntity<Void> deleteEstrategia(@PathVariable String id) {
        inovacaoService.deleteEstrategia(id);
        return ResponseEntity.ok().build();
    }

    // Ideias
    @GetMapping("/ideias")
    public ResponseEntity<List<Ideia>> getAllIdeias() {
        return ResponseEntity.ok(inovacaoService.getAllIdeias());
    }

    @PostMapping("/ideias")
    public ResponseEntity<Ideia> addIdeia(@RequestBody Ideia ideia) {
        if (ideia.getId() != null && ideia.getId().isEmpty()) ideia.setId(null);
        return ResponseEntity.ok(inovacaoService.addIdeia(ideia));
    }

    @PutMapping("/ideias/{id}")
    public ResponseEntity<Ideia> updateIdeia(@PathVariable String id, @RequestBody Ideia ideia) {
        return ResponseEntity.ok(inovacaoService.updateIdeia(id, ideia));
    }

    @DeleteMapping("/ideias/{id}")
    public ResponseEntity<Void> deleteIdeia(@PathVariable String id) {
        inovacaoService.deleteIdeia(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ideias/{id}/votar")
    public ResponseEntity<Ideia> votarIdeia(@PathVariable String id) {
        return ResponseEntity.ok(inovacaoService.votarIdeia(id));
    }

    @PostMapping("/ideias/{id}/comentar")
    public ResponseEntity<Ideia> comentarIdeia(@PathVariable String id, @RequestBody Comentario comentario) {
        return ResponseEntity.ok(inovacaoService.comentarIdeia(id, comentario));
    }

    // Transacoes Financeiras (Receitas e Despesas)
    @GetMapping("/transacoes")
    public ResponseEntity<List<TransacaoFinanceira>> getTransacoes(
            @RequestParam(required = false) String projetoId,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(inovacaoService.getTransacoes(projetoId, tipo));
    }

    @PostMapping("/transacoes")
    public ResponseEntity<TransacaoFinanceira> addTransacao(@RequestBody TransacaoFinanceira transacao) {
        return ResponseEntity.ok(inovacaoService.addTransacao(transacao));
    }

    @GetMapping("/receitas")
    public ResponseEntity<List<TransacaoFinanceira>> getReceitas(
            @RequestParam(required = false) String projetoId) {
        return ResponseEntity.ok(inovacaoService.getReceitas(projetoId));
    }

    @PostMapping("/receitas")
    public ResponseEntity<TransacaoFinanceira> addReceita(@RequestBody TransacaoFinanceira receita) {
        return ResponseEntity.ok(inovacaoService.addReceita(receita));
    }

    @GetMapping("/despesas")
    public ResponseEntity<List<TransacaoFinanceira>> getDespesas(
            @RequestParam(required = false) String projetoId) {
        return ResponseEntity.ok(inovacaoService.getDespesas(projetoId));
    }

    @PostMapping("/despesas")
    public ResponseEntity<TransacaoFinanceira> addDespesa(@RequestBody TransacaoFinanceira despesa) {
        return ResponseEntity.ok(inovacaoService.addDespesa(despesa));
    }

    @DeleteMapping("/transacoes/{id}")
    public ResponseEntity<Void> deleteTransacao(@PathVariable String id) {
        inovacaoService.deleteTransacao(id);
        return ResponseEntity.ok().build();
    }

    // Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<br.com.inovagab.dto.response.DashboardResumoResponse> getDashboardResumo() {
        return ResponseEntity.ok(inovacaoService.getDashboardResumo());
    }
}
