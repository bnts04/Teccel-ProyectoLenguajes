package com.teccell.backend.dto.response;

public record DashboardTecnicoResponse(
    long misOrdenesActivas,
    long ordenesEnReparacion,
    long misVencidas,
    long misProximasEntregas,
    long casosAtencionHoy
){}
