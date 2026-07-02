package com.teccell.backend.dto.response;
import com.teccell.backend.enums.NivelReincidenciaEquipo;
public record ReincidenciaEquipoResponse (
    Long equipoId,
    String codigoInterno,
    String marcaModelo,
    long totalReparacionesAnteriores,
    NivelReincidenciaEquipo nivel
)
{}