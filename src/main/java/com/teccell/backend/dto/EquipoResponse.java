package com.teccell.backend.dto;

import java.time.LocalDateTime;

public record EquipoResponse(
        Long id,
        String codigoInterno,
        Long clienteId,
        String nombreCliente,
        String tipo,
        String descripcionTipoOtro,
        String marca,
        String modelo,
        String color,
        String caracteristicasFisicas,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}