package com.teccell.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarDiagnosticoRequest(

        @NotBlank(message = "El diagnóstico es obligatorio")
        @Size(max = 800, message = "El diagnóstico no debe superar los 800 caracteres")
        String diagnostico,

        @Size(max = 500, message = "La observación no debe superar los 500 caracteres")
        String observacion
) {
}