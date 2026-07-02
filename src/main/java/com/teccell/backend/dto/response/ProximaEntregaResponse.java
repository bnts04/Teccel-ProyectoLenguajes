package com.teccell.backend.dto.response;
import java.time.LocalDate;
public record ProximaEntregaResponse(
    Long id,
    LocalDate fechaEntrega,
    String nombreCliente
) {}
