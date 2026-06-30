package com.teccell.backend.dto.request;

import com.teccell.backend.enums.PrioridadOrden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiarPrioridadRequest(

        @NotNull(message = "La nueva prioridad es obligatoria")
        PrioridadOrden nuevaPrioridad,

        @NotBlank(message = "El motivo del cambio es obligatorio")
        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}