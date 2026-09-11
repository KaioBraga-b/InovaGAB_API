package br.com.inovagab.controller;

import br.com.inovagab.dto.response.DashboardResumoResponse;
import br.com.inovagab.dto.response.ProjetoResponse;
import br.com.inovagab.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dashboard", description = "KPIs executivos e relatórios analíticos (LIDER e GESTOR)")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LIDER','GESTOR')")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Resumo geral — ROI total, lucros, investimento, engajamento e produtividade")
    @GetMapping("/resumo-geral")
    public ResponseEntity<DashboardResumoResponse> resumoGeral() {
        return ResponseEntity.ok(dashboardService.resumoGeral());
    }

    @Operation(summary = "Resumo de uma estratégia específica com retornos dos projetos vinculados")
    @GetMapping("/estrategia/{estrategiaId}")
    public ResponseEntity<DashboardResumoResponse> porEstrategia(
            @PathVariable String estrategiaId) {
        return ResponseEntity.ok(dashboardService.resumoPorEstrategia(estrategiaId));
    }

    @Operation(summary = "Detalhamento de métricas individuais de um projeto (ROI, investimento vs retorno)")
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<ProjetoResponse> porProjeto(@PathVariable String projetoId) {
        return ResponseEntity.ok(dashboardService.detalheProjeto(projetoId));
    }
}
