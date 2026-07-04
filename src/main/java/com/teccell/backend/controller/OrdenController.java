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
import com.teccell.backend.dto.request.CambiarFechaEstimadaRequest;
import com.teccell.backend.dto.request.CambiarPrecioRequest;
import com.teccell.backend.dto.request.CambiarPrioridadRequest;
import com.teccell.backend.dto.request.CancelarOrdenRequest;
import com.teccell.backend.dto.request.ReasignarOrdenRequest;
import com.teccell.backend.dto.request.RegistrarEntregaRequest;
import com.teccell.backend.dto.response.PaginaResponse;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.PrioridadOrden;

import java.time.LocalDate;


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

    @GetMapping("/buscar")
    public PaginaResponse<OrdenResponse> buscarOrdenes(
            @RequestParam(required = false) EstadoOrden estado,
            @RequestParam(required = false) PrioridadOrden prioridad,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String ticket,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ordenService.buscarOrdenes(
                estado,
                prioridad,
                tecnicoId,
                cliente,
                ticket,
                fechaDesde,
                fechaHasta,
                page,
                size,
                sortBy,
                sortDir
        );
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

    @PutMapping("/{id}/precio")
    public OrdenResponse cambiarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPrecioRequest request
    ) {
        return ordenService.cambiarPrecio(id, request);
    }

    @PutMapping("/{id}/fecha-estimada")
    public OrdenResponse cambiarFechaEstimada(
            @PathVariable Long id,
            @Valid @RequestBody CambiarFechaEstimadaRequest request
    ) {
        return ordenService.cambiarFechaEstimada(id, request);
    }

    @PutMapping("/{id}/prioridad")
    public OrdenResponse cambiarPrioridad(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPrioridadRequest request
    ) {
        return ordenService.cambiarPrioridad(id, request);
    }

    @PostMapping("/{id}/entregar")
    public OrdenResponse registrarEntrega(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarEntregaRequest request
    ) {
        return ordenService.registrarEntrega(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public OrdenResponse cancelarOrden(
            @PathVariable Long id,
            @Valid @RequestBody CancelarOrdenRequest request
    ) {
        return ordenService.cancelarOrden(id, request);
    }

    @PutMapping("/{id}/reasignar")
    public OrdenResponse reasignarOrden(
            @PathVariable Long id,
            @Valid @RequestBody ReasignarOrdenRequest request
    ) {
        return ordenService.reasignarOrden(id, request);
    }

}