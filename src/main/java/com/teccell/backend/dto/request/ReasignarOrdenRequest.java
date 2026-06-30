package com.teccell.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReasignarOrdenRequest(

        @NotNull(message = "El nuevo técnico es obligatorio")
        Long nuevoTecnicoId,

        @NotBlank(message = "El motivo de reasignación es obligatorio")
        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}