package com.teccell.backend.dto.response;
import java.time.LocalDate;
public record OrdenVencidaResponse(
    Long id,
    LocalDate fechaEntrega,
    String estado
) {
    
}
