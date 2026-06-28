package com.teccell.backend.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String username,
        String rol,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}