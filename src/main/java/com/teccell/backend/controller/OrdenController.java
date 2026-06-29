package com.teccell.backend.controller;

import com.teccell.backend.dto.request.CrearOrdenRequest;
import com.teccell.backend.dto.response.OrdenResponse;
import com.teccell.backend.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.teccell.backend.dto.request.CambiarEstadoRequest;
import com.teccell.backend.dto.request.RegistrarAvanceRequest;
import com.teccell.backend.dto.request.RegistrarDiagnosticoRequest;
import com.teccell.backend.dto.response.HistorialOrdenResponse;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public OrdenResponse crearOrden(@Valid @RequestBody CrearOrdenRequest request) {
        return ordenService.crearOrden(request);
    }

    @GetMapping
    public List<OrdenResponse> listarOrdenes() {
        return ordenService.listarOrdenes();
    }

    @GetMapping("/{id}")
    public OrdenResponse obtenerOrden(@PathVariable Long id) {
        return ordenService.obtenerOrden(id);
    }

    @GetMapping("/ticket/{ticket}")
    public OrdenResponse buscarPorTicketInterno(@PathVariable String ticket) {
        return ordenService.buscarPorTicketInterno(ticket);
    }

    @GetMapping("/equipo/{equipoId}")
    public List<OrdenResponse> listarOrdenesPorEquipo(@PathVariable Long equipoId) {
        return ordenService.listarOrdenesPorEquipo(equipoId);
    }

    @PutMapping("/{id}/estado")
    public OrdenResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        return ordenService.cambiarEstado(id, request);
    }

    @PutMapping("/{id}/diagnostico")
    public OrdenResponse registrarDiagnostico(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarDiagnosticoRequest request
    ) {
        return ordenService.registrarDiagnostico(id, request);
    }

    @PostMapping("/{id}/avances")
    public HistorialOrdenResponse registrarAvance(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarAvanceRequest request
    ) {
        return ordenService.registrarAvance(id, request);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialOrdenResponse> listarHistorial(@PathVariable Long id) {
        return ordenService.listarHistorial(id);
    }
}