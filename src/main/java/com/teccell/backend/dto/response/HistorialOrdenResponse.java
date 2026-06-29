package com.teccell.backend.dto.response;

import java.time.LocalDateTime;

public record HistorialOrdenResponse(
        Long id,
        Long ordenId,
        String ticket,
        String tipoEvento,
        String estadoAnterior,
        String estadoNuevo,
        String descripcion,
        String detalleAnterior,
        String detalleNuevo,
        Long usuarioResponsableId,
        String nombreUsuarioResponsable,
        LocalDateTime fechaEvento
) {
}