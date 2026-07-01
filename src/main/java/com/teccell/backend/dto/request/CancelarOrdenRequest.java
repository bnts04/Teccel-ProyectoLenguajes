package com.teccell.backend.dto.request;

import com.teccell.backend.enums.MotivoCancelacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelarOrdenRequest(

        @NotNull(message = "El motivo de cancelación es obligatorio")
        MotivoCancelacion motivoCancelacion,

        @Size(max = 500, message = "La descripción de cancelación no debe superar los 500 caracteres")
        String descripcionCancelacion
) {
}