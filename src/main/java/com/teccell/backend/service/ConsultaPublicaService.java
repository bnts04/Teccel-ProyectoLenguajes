package com.teccell.backend.service;

import com.teccell.backend.dto.response.ConsultaPublicaResponse;
import com.teccell.backend.dto.response.HistorialPublicoOrdenResponse;
import com.teccell.backend.dto.response.RecuperacionTicketResponse;
import com.teccell.backend.entity.HistorialOrden;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.TipoEventoHistorial;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.HistorialOrdenRepository;
import com.teccell.backend.repository.OrdenReparacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultaPublicaService {

    private final OrdenReparacionRepository ordenRepository;
    private final HistorialOrdenRepository historialOrdenRepository;

    public ConsultaPublicaResponse consultarPorTicket(String ticket) {
        OrdenReparacion orden = ordenRepository.findByTicket(ticket.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna orden con el ticket: " + ticket));

        if (!Boolean.TRUE.equals(orden.getActivo())) {
            throw new ResourceNotFoundException("La orden solicitada no está disponible para consulta.");
        }

        ConsultaPublicaResponse response = new ConsultaPublicaResponse();

        response.setTicket(orden.getTicket());
        response.setTipoEquipo(orden.getEquipo().getTipo().name());
        response.setMarcaModelo(orden.getEquipo().getMarca() + " " + orden.getEquipo().getModelo());
        response.setFallaReportada(orden.getFallaReportada());
        response.setDiagnostico(orden.getDiagnostico());
        response.setEstado(orden.getEstado().name());
        response.setPrecioAcordado(
                orden.getPrecioAcordado() != null
                        ? orden.getPrecioAcordado().doubleValue()
                        : null
        );
        response.setFechaIngreso(
                orden.getFechaIngreso() != null
                        ? orden.getFechaIngreso().toLocalDate()
                        : null
        );
        response.setFechaEstimada(orden.getFechaEstimadaEntrega());

        String nombreCompleto = orden.getEquipo().getCliente().getNombres()
                + " "
                + orden.getEquipo().getCliente().getApellidos();

        response.setClienteOculto(ofuscarNombre(nombreCompleto));

        List<HistorialPublicoOrdenResponse> lineaTiempo = new ArrayList<>();

        HistorialPublicoOrdenResponse eventoRecepcion = new HistorialPublicoOrdenResponse();
        eventoRecepcion.setEvento("Orden recibida");
        eventoRecepcion.setNotas("La orden fue registrada correctamente en el sistema.");
        eventoRecepcion.setFechaEvento(orden.getFechaIngreso());

        lineaTiempo.add(eventoRecepcion);

        List<HistorialPublicoOrdenResponse> eventosHistorial = historialOrdenRepository
                .findByOrdenIdOrderByFechaEventoAsc(orden.getId())
                .stream()
                .filter(historial -> historial.getTipoEvento() != TipoEventoHistorial.CREACION)
                .map(this::convertirHistorialPublico)
                .filter(Objects::nonNull)
                .toList();

        lineaTiempo.addAll(eventosHistorial);

        response.setLineaTiempo(lineaTiempo);

        return response;
    }

    public List<RecuperacionTicketResponse> recuperarPorTelefono(String telefono) {
        return ordenRepository.findByClienteTelefono(telefono.trim())
                .stream()
                .map(o -> new RecuperacionTicketResponse(
                        o.getTicket(),
                        o.getEquipo().getMarca(),
                        o.getEstado().name()
                ))
                .collect(Collectors.toList());
    }

    public List<RecuperacionTicketResponse> recuperarPorDniParcial(String ultimos4) {
        return ordenRepository.findByClienteDniTerminaCon(ultimos4.trim())
                .stream()
                .map(o -> new RecuperacionTicketResponse(
                        o.getTicket(),
                        o.getEquipo().getMarca(),
                        o.getEstado().name()
                ))
                .collect(Collectors.toList());
    }

    public List<RecuperacionTicketResponse> recuperarPorDniYTelefono(String ultimos4, String telefono) {
        return ordenRepository.findByClienteDniTerminaConAndTelefono(ultimos4.trim(), telefono.trim())
                .stream()
                .map(o -> new RecuperacionTicketResponse(
                        o.getTicket(),
                        o.getEquipo().getMarca(),
                        o.getEstado().name()
                ))
                .collect(Collectors.toList());
    }

    private HistorialPublicoOrdenResponse convertirHistorialPublico(HistorialOrden historial) {
        TipoEventoHistorial tipoEvento = historial.getTipoEvento();

        if (tipoEvento == TipoEventoHistorial.CAMBIO_PRECIO
                || tipoEvento == TipoEventoHistorial.CAMBIO_PRIORIDAD
                || tipoEvento == TipoEventoHistorial.REASIGNACION) {
            return null;
        }

        HistorialPublicoOrdenResponse response = new HistorialPublicoOrdenResponse();
        response.setFechaEvento(historial.getFechaEvento());

        switch (tipoEvento) {
            case CREACION -> {
                response.setEvento("Orden recibida");
                response.setNotas("La orden fue registrada correctamente en el sistema.");
            }

            case CAMBIO_ESTADO -> {
                EstadoOrden estadoNuevo = historial.getEstadoNuevo();

                if (estadoNuevo == null) {
                    response.setEvento("Actualización de estado");
                    response.setNotas("Se actualizó el estado de la orden.");
                    return response;
                }

                switch (estadoNuevo) {
                    case RECIBIDO -> {
                        response.setEvento("Orden recibida");
                        response.setNotas("La orden se encuentra registrada.");
                    }
                    case EN_DIAGNOSTICO -> {
                        response.setEvento("En diagnóstico");
                        response.setNotas("El equipo se encuentra en revisión técnica.");
                    }
                    case EN_REPARACION -> {
                        response.setEvento("En reparación");
                        response.setNotas("El equipo se encuentra en proceso de reparación.");
                    }
                    case LISTO_PARA_RECOGER -> {
                        response.setEvento("Listo para recoger");
                        response.setNotas("El equipo está listo para ser recogido.");
                    }
                    case ENTREGADO -> {
                        response.setEvento("Entregado");
                        response.setNotas("El equipo fue entregado al cliente.");
                    }
                    case CANCELADO -> {
                        response.setEvento("Cancelado");
                        response.setNotas("La orden fue cancelada.");
                    }
                }
            }

            case DIAGNOSTICO -> {
                response.setEvento("Diagnóstico registrado");
                response.setNotas("Se registró el diagnóstico técnico del equipo.");
            }

            case AVANCE -> {
                response.setEvento("Avance de reparación");
                response.setNotas("Se registró un avance en la atención del equipo.");
            }

            case CAMBIO_FECHA -> {
                response.setEvento("Fecha estimada actualizada");
                response.setNotas("Se actualizó la fecha estimada de entrega.");
            }

            case CANCELACION -> {
                response.setEvento("Cancelado");
                response.setNotas("La orden fue cancelada.");
            }

            case ENTREGA -> {
                response.setEvento("Entregado");
                response.setNotas("El equipo fue entregado al cliente.");
            }

            default -> {
                return null;
            }
        }

        return response;
    }

    private String ofuscarNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return "Cliente Anonimizado";
        }

        String[] partes = nombreCompleto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String parte : partes) {
            if (!parte.isBlank()) {
                resultado.append(parte.charAt(0)).append("*** ");
            }
        }

        return resultado.toString().trim();
    }
}