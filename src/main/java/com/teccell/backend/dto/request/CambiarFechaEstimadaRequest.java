package com.teccell.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiarFechaEstimadaRequest(

        @NotNull(message = "Los nuevos días estimados son obligatorios")
        @Min(value = 1, message = "Los días estimados deben ser como mínimo 1")
        Integer nuevosDiasEstimados,

        @NotBlank(message = "El motivo del cambio es obligatorio")
        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}