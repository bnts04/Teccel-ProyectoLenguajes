package com.teccell.backend.dto.response;

import java.time.LocalDate;

public record OrdenVencidaResponse(
        Long ordenId,
        String ticket,
        LocalDate fechaEstimadaEntrega,
        long diasVencidos,
        String nombreCliente,
        String estado
) {
}