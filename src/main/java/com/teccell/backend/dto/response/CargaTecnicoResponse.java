package com.teccell.backend.dto.response;

public record CargaTecnicoResponse(
    String nombreTecnico,
    long cantidadOrdenesAsignadas
) {}
