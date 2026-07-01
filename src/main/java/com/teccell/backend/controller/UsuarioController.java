package com.teccell.backend.controller;

import com.teccell.backend.dto.request.CrearTecnicoRequest;
import com.teccell.backend.dto.request.RestablecerPasswordRequest;
import com.teccell.backend.dto.response.UsuarioResponse;
import com.teccell.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/tecnicos")
    public UsuarioResponse crearTecnico(@Valid @RequestBody CrearTecnicoRequest request) {
        return usuarioService.crearTecnico(request);
    }

    @GetMapping("/tecnicos")
    public List<UsuarioResponse> listarTecnicos() {
        return usuarioService.listarTecnicos();
    }

    @GetMapping("/tecnicos/{id}")
    public UsuarioResponse obtenerTecnico(@PathVariable Long id) {
        return usuarioService.obtenerTecnico(id);
    }

    @PatchMapping("/tecnicos/{id}/estado")
    public UsuarioResponse cambiarEstadoTecnico(
            @PathVariable Long id,
            @RequestParam boolean activo
    ) {
        return usuarioService.cambiarEstadoTecnico(id, activo);
    }

    @PutMapping("/tecnicos/{id}/restablecer-password")
    public UsuarioResponse restablecerPassword(
            @PathVariable Long id,
            @Valid @RequestBody RestablecerPasswordRequest request
    ) {
        return usuarioService.restablecerPassword(id, request);
    }
}