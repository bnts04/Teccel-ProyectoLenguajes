package com.teccell.backend.dto;

public record LoginResponse(
        Long id,
        String nombreCompleto,
        String username,
        String rol,
        String mensaje
) {
}