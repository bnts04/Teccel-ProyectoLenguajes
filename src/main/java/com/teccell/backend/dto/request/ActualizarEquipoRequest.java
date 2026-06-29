package com.teccell.backend.dto.request;

import com.teccell.backend.enums.TipoEquipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarEquipoRequest(

        @NotNull(message = "El tipo de equipo es obligatorio")
        TipoEquipo tipo,

        @Size(max = 80, message = "La descripción del tipo no debe superar los 80 caracteres")
        String descripcionTipoOtro,

        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 80, message = "La marca no debe superar los 80 caracteres")
        String marca,

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 80, message = "El modelo no debe superar los 80 caracteres")
        String modelo,

        @Size(max = 50, message = "El color no debe superar los 50 caracteres")
        String color,

        @Size(max = 300, message = "Las características físicas no deben superar los 300 caracteres")
        String caracteristicasFisicas,

        Boolean activo
) {
}