package com.teccell.backend.dto.response;

public record CargaTecnicoResponse(
        Long tecnicoId,
        String nombreTecnico,
        long cantidadOrdenesAsignadas
) {
}