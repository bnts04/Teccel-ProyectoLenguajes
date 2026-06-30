package com.teccell.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CambiarPrecioRequest(

        @NotNull(message = "El nuevo precio es obligatorio")
        @DecimalMin(value = "0.00", message = "El nuevo precio no puede ser negativo")
        BigDecimal nuevoPrecio,

        @NotBlank(message = "El motivo del cambio es obligatorio")
        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}