package com.teccell.backend.dto.request;

import com.teccell.backend.enums.PrioridadOrden;
import com.teccell.backend.enums.TipoAccesorio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record CrearOrdenRequest(

        @NotNull(message = "El ID del equipo es obligatorio")
        Long equipoId,

        Long tecnicoResponsableId,

        @NotBlank(message = "La falla reportada es obligatoria")
        @Size(max = 500, message = "La falla reportada no debe superar los 500 caracteres")
        String fallaReportada,

        @Size(max = 300, message = "El estado físico de recepción no debe superar los 300 caracteres")
        String estadoFisicoRecepcion,

        Set<TipoAccesorio> accesoriosEntregados,

        @Size(max = 200, message = "La descripción de otros accesorios no debe superar los 200 caracteres")
        String otrosAccesorios,

        @NotNull(message = "El precio acordado es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio acordado no puede ser negativo")
        BigDecimal precioAcordado,

        @NotNull(message = "Los días estimados son obligatorios")
        @Min(value = 1, message = "Los días estimados deben ser como mínimo 1")
        Integer diasEstimados,

        PrioridadOrden prioridad
) {
}