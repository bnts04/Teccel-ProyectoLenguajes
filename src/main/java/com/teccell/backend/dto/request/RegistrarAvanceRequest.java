package com.teccell.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarAvanceRequest(

        @NotBlank(message = "La descripción del avance es obligatoria")
        @Size(max = 800, message = "El avance no debe superar los 800 caracteres")
        String descripcion
) {
}