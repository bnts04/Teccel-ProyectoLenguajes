package com.teccell.backend.controller;

import com.teccell.backend.dto.ActualizarEquipoRequest;
import com.teccell.backend.dto.CrearEquipoRequest;
import com.teccell.backend.dto.EquipoResponse;
import com.teccell.backend.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @PostMapping
    public EquipoResponse crearEquipo(@Valid @RequestBody CrearEquipoRequest request) {
        return equipoService.crearEquipo(request);
    }

    @GetMapping
    public List<EquipoResponse> listarEquipos() {
        return equipoService.listarEquipos();
    }

    @GetMapping("/{id}")
    public EquipoResponse obtenerEquipo(@PathVariable Long id) {
        return equipoService.obtenerEquipo(id);
    }

    @GetMapping("/codigo/{codigoInterno}")
    public EquipoResponse buscarPorCodigoInterno(@PathVariable String codigoInterno) {
        return equipoService.buscarPorCodigoInterno(codigoInterno);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<EquipoResponse> listarEquiposPorCliente(@PathVariable Long clienteId) {
        return equipoService.listarEquiposPorCliente(clienteId);
    }

    @GetMapping("/cliente/{clienteId}/activos")
    public List<EquipoResponse> listarEquiposActivosPorCliente(@PathVariable Long clienteId) {
        return equipoService.listarEquiposActivosPorCliente(clienteId);
    }

    @PutMapping("/{id}")
    public EquipoResponse actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEquipoRequest request
    ) {
        return equipoService.actualizarEquipo(id, request);
    }
}