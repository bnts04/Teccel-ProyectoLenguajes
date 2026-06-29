package com.teccell.backend.dto.response;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String telefono,
        String dni,
        String correo,
        String direccion,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}