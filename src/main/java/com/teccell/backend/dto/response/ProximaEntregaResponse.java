package com.teccell.backend.dto.response;

import java.time.LocalDate;

public record ProximaEntregaResponse(
        Long ordenId,
        String ticket,
        LocalDate fechaEstimadaEntrega,
        String nombreCliente,
        String estado,
        String prioridad
) {
}