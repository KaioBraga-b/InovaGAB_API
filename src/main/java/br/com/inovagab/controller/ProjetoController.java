package br.com.inovagab.controller;

import br.com.inovagab.dto.request.ProjetoRequest;
import br.com.inovagab.dto.response.ProjetoResponse;
import br.com.inovagab.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Projetos", description = "Cadastro e acompanhamento de projetos e iniciativas")
@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @Operation(summary = "Lista todos os projetos com etapas e progresso (todos os autenticados)")
    @GetMapping
    public ResponseEntity<List<ProjetoResponse>> listar() {
        return ResponseEntity.ok(projetoService.listarTodos());
    }

    @Operation(summary = "Cria novo projeto/iniciativa (GESTOR)")
    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoResponse> criar(
            @Valid @RequestBody ProjetoRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal br.com.inovagab.security.UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.criar(request, principal));
    }

    @Operation(summary = "Atualiza progresso, etapa e dados financeiros do projeto (GESTOR)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoResponse> atualizar(
            @PathVariable String id,
            @Valid @RequestBody ProjetoRequest request) {
        return ResponseEntity.ok(projetoService.atualizar(id, request));
    }

    @Operation(summary = "Remove projeto (GESTOR ou LIDER)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        projetoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
