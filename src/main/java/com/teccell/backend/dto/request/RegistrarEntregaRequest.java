package com.teccell.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarEntregaRequest(

        @NotBlank(message = "El nombre de quien recoge es obligatorio")
        @Size(max = 120, message = "El nombre de quien recoge no debe superar los 120 caracteres")
        String nombreRecoge,

        @Size(max = 15, message = "El DNI de quien recoge no debe superar los 15 caracteres")
        String dniRecoge,

        @Size(max = 500, message = "La observación de entrega no debe superar los 500 caracteres")
        String observacionEntrega
) {
}