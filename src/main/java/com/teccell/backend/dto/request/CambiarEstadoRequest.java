package com.teccell.backend.dto.request;

import com.teccell.backend.enums.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiarEstadoRequest(

        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoOrden nuevoEstado,

        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}