package com.teccell.backend.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teccell.backend.dto.response.AlertaOrdenResponse;
import com.teccell.backend.dto.response.ReincidenciaEquipoResponse;
import com.teccell.backend.service.AlertaOrdenService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")

public class AlertaController {
 private final AlertaOrdenService alertaService;

    public AlertaController(AlertaOrdenService alertaService) {
        this.alertaService = alertaService;
    }
    @GetMapping("/ordenes/vencidas")
    public ResponseEntity<List<AlertaOrdenResponse>> obtenerVencidas() {
        return ResponseEntity.ok(alertaService.getOrdenesVencidas());
    }


    @GetMapping("/ordenes/proximas-entregas")
    public ResponseEntity<List<AlertaOrdenResponse>> obtenerProximasEntregas() {
        return ResponseEntity.ok(alertaService.getProximasEntregas());
    }


    @GetMapping("/equipos/{id}/reincidencia")
    public ResponseEntity<ReincidenciaEquipoResponse> obtenerReincidencia(@PathVariable("id") Long id) {
        return ResponseEntity.ok(alertaService.calcularReincidencia(id));
    }   
}
