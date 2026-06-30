package com.teccell.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record OrdenResponse(
        Long id,
        String ticket,

        Long equipoId,
        String codigoInternoEquipo,
        String tipoEquipo,
        String marcaEquipo,
        String modeloEquipo,
        String colorEquipo,

        Long clienteId,
        String nombreCliente,
        String telefonoCliente,

        Long tecnicoId,
        String nombreTecnico,

        String fallaReportada,
        String diagnostico,
        String estadoFisicoRecepcion,
        Set<String> accesoriosEntregados,
        String otrosAccesorios,

        BigDecimal precioAcordado,
        Integer diasEstimados,
        LocalDateTime fechaIngreso,
        LocalDate fechaEstimadaEntrega,

        String prioridad,
        String estado,
        Boolean activo,
        LocalDateTime fechaCreacion,

        String nombreRecoge,
        String dniRecoge,
        String observacionEntrega,
        LocalDateTime fechaEntrega,
        Long tecnicoEntregaId,
        String nombreTecnicoEntrega,

        String motivoCancelacion,
        String descripcionCancelacion,
        LocalDateTime fechaCancelacion,
        Long usuarioCancelacionId,
        String nombreUsuarioCancelacion
) {
}