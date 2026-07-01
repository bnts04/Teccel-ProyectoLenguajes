package com.teccell.backend.dto.response;

public record LoginResponse(
        Long id,
        String nombreCompleto,
        String username,
        String rol,
        String mensaje
) {
}