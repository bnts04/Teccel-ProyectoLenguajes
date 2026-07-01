package com.teccell.backend.service;

import com.teccell.backend.dto.response.HistorialOrdenResponse;
import com.teccell.backend.entity.HistorialOrden;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.entity.Usuario;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.TipoEventoHistorial;
import com.teccell.backend.repository.HistorialOrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialOrdenRepository historialOrdenRepository;

    public void registrarEvento(
            OrdenReparacion orden,
            Usuario usuarioResponsable,
            TipoEventoHistorial tipoEvento,
            EstadoOrden estadoAnterior,
            EstadoOrden estadoNuevo,
            String descripcion,
            String detalleAnterior,
            String detalleNuevo
    ) {
        HistorialOrden historial = HistorialOrden.builder()
                .orden(orden)
                .usuarioResponsable(usuarioResponsable)
                .tipoEvento(tipoEvento)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .descripcion(descripcion)
                .detalleAnterior(detalleAnterior)
                .detalleNuevo(detalleNuevo)
                .build();

        historialOrdenRepository.save(historial);
    }

    public List<HistorialOrdenResponse> listarHistorialPorOrden(Long ordenId) {
        return historialOrdenRepository.findByOrdenIdOrderByFechaEventoAsc(ordenId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private HistorialOrdenResponse convertirAResponse(HistorialOrden historial) {
        String estadoAnterior = historial.getEstadoAnterior() != null
                ? historial.getEstadoAnterior().name()
                : null;

        String estadoNuevo = historial.getEstadoNuevo() != null
                ? historial.getEstadoNuevo().name()
                : null;

        return new HistorialOrdenResponse(
                historial.getId(),
                historial.getOrden().getId(),
                historial.getOrden().getTicket(),
                historial.getTipoEvento().name(),
                estadoAnterior,
                estadoNuevo,
                historial.getDescripcion(),
                historial.getDetalleAnterior(),
                historial.getDetalleNuevo(),
                historial.getUsuarioResponsable().getId(),
                historial.getUsuarioResponsable().getNombreCompleto(),
                historial.getFechaEvento()
        );
    }
}