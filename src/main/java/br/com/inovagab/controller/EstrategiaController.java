package br.com.inovagab.controller;

import br.com.inovagab.dto.request.EstrategiaRequest;
import br.com.inovagab.dto.response.EstrategiaResponse;
import br.com.inovagab.dto.response.HistoricoEstrategiaResponse;
import br.com.inovagab.service.EstrategiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Estratégias", description = "Orientações estratégicas — CRUD exclusivo da Liderança")
@RestController
@RequestMapping("/api/estrategias")
@RequiredArgsConstructor
public class EstrategiaController {

    private final EstrategiaService estrategiaService;

    @Operation(summary = "Lista todas as orientações estratégicas (todos os perfis)")
    @GetMapping
    public ResponseEntity<List<EstrategiaResponse>> listar() {
        return ResponseEntity.ok(estrategiaService.listarTodas());
    }

    @Operation(summary = "Cria nova orientação estratégica (LIDER)")
    @PostMapping
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<EstrategiaResponse> criar(
            @Valid @RequestBody EstrategiaRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal br.com.inovagab.security.UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estrategiaService.criar(request, principal));
    }

    @Operation(summary = "Atualiza orientação estratégica (LIDER)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<EstrategiaResponse> atualizar(
            @PathVariable String id,
            @Valid @RequestBody EstrategiaRequest request) {
        return ResponseEntity.ok(estrategiaService.atualizar(id, request));
    }

    @Operation(summary = "Remove orientação estratégica (LIDER)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        estrategiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Histórico das estratégias (todos os autenticados)")
    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoEstrategiaResponse>> historico() {
        return ResponseEntity.ok(estrategiaService.listarHistorico());
    }
}
