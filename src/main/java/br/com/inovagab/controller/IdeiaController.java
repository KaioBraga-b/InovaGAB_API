package br.com.inovagab.controller;

import br.com.inovagab.dto.request.IdeiaRequest;
import br.com.inovagab.dto.request.IdeiaStatusRequest;
import br.com.inovagab.dto.response.IdeiaResponse;
import br.com.inovagab.security.UserPrincipal;
import br.com.inovagab.service.IdeiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ideias", description = "Cadastro, curadoria e votação de ideias de inovação")
@RestController
@RequestMapping("/api/ideias")
@RequiredArgsConstructor
public class IdeiaController {

    private final IdeiaService ideiaService;

    @Operation(summary = "Lista todas as ideias — filtrável por area ou status (GESTOR, LIDER)")
    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    public ResponseEntity<List<IdeiaResponse>> listar(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ideiaService.listarTodas(area, status));
    }

    @Operation(summary = "Lista ideias do próprio usuário (OPERADOR, GESTOR)")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('OPERADOR','GESTOR','LIDER')")
    public ResponseEntity<List<IdeiaResponse>> listarPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(ideiaService.listarPorUsuario(userId));
    }

    @Operation(summary = "Cadastra nova ideia (todos os perfis)")
    @PostMapping
    public ResponseEntity<IdeiaResponse> criar(
            @Valid @RequestBody IdeiaRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ideiaService.criar(request, principal));
    }

    @Operation(summary = "Atualiza ideia — autor ou GESTOR/LIDER")
    @PutMapping("/{id}")
    public ResponseEntity<IdeiaResponse> atualizar(
            @PathVariable String id,
            @Valid @RequestBody IdeiaRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ideiaService.atualizar(id, request, principal));
    }

    @Operation(summary = "Altera status da ideia — curadoria do GESTOR")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaResponse> atualizarStatus(
            @PathVariable String id,
            @Valid @RequestBody IdeiaStatusRequest request) {
        return ResponseEntity.ok(ideiaService.atualizarStatus(id, request));
    }

    @Operation(summary = "Vota/apoia uma ideia (todos os autenticados)")
    @PostMapping("/{id}/vote")
    public ResponseEntity<IdeiaResponse> votar(@PathVariable String id) {
        return ResponseEntity.ok(ideiaService.votar(id));
    }

    @Operation(summary = "Remove ideia — autor ou GESTOR/LIDER")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        ideiaService.deletar(id, principal);
        return ResponseEntity.noContent().build();
    }
}
