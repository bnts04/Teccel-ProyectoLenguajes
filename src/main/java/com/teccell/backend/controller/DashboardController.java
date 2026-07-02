package com.teccell.backend.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teccell.backend.dto.response.CargaTecnicoResponse;
import com.teccell.backend.dto.response.DashboardAdminResponse;
import com.teccell.backend.dto.response.DashboardTecnicoResponse;
import com.teccell.backend.dto.response.OrdenVencidaResponse;
import com.teccell.backend.dto.response.ProximaEntregaResponse;
import com.teccell.backend.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    public ResponseEntity<DashboardAdminResponse> getDashboardAdmin() {
        return ResponseEntity.ok(dashboardService.getDashboardAdmin());
    }

    @GetMapping("/carga-tecnicos")
    public ResponseEntity<List<CargaTecnicoResponse>> getCargaTecnicos() {
        return ResponseEntity.ok(dashboardService.getCargaPorTecnico());
    }

    @GetMapping("/tecnico/{id}")
    public ResponseEntity<DashboardTecnicoResponse> getDashboardTecnico(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getDashboardTecnico(id));
    }

    @GetMapping("/proximas-entregas")
    public ResponseEntity<List<ProximaEntregaResponse>> getProximasEntregas() {
    return ResponseEntity.ok(dashboardService.getProximasEntregas());
    }

    @GetMapping("/vencidas")
    public ResponseEntity<List<OrdenVencidaResponse>> getOrdenesVencidas() {
    return ResponseEntity.ok(dashboardService.getOrdenesVencidas());
    }


}
