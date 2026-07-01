package com.teccell.backend.dto.response;

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