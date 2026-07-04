package com.teccell.backend.service;

import com.teccell.backend.dto.response.CargaTecnicoResponse;
import com.teccell.backend.dto.response.DashboardAdminResponse;
import com.teccell.backend.dto.response.DashboardTecnicoResponse;
import com.teccell.backend.dto.response.OrdenVencidaResponse;
import com.teccell.backend.dto.response.ProximaEntregaResponse;
import com.teccell.backend.entity.Cliente;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.entity.Usuario;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.RolUsuario;
import com.teccell.backend.repository.OrdenReparacionRepository;
import com.teccell.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final OrdenReparacionRepository ordenRepo;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(
            OrdenReparacionRepository ordenRepo,
            UsuarioRepository usuarioRepository
    ) {
        this.ordenRepo = ordenRepo;
        this.usuarioRepository = usuarioRepository;
    }

    public DashboardAdminResponse getDashboardAdmin() {
        List<OrdenReparacion> todasLasOrdenes = ordenRepo.findAll();
        LocalDate hoy = LocalDate.now();

        long activas = todasLasOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .count();

        long recibidas = contarPorEstado(todasLasOrdenes, EstadoOrden.RECIBIDO);
        long enDiagnostico = contarPorEstado(todasLasOrdenes, EstadoOrden.EN_DIAGNOSTICO);
        long enReparacion = contarPorEstado(todasLasOrdenes, EstadoOrden.EN_REPARACION);
        long listasParaRecoger = contarPorEstado(todasLasOrdenes, EstadoOrden.LISTO_PARA_RECOGER);

        long vencidas = todasLasOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> orden.getFechaEstimadaEntrega().isBefore(hoy))
                .count();

        BigDecimal sumaPreciosAcordados = todasLasOrdenes.stream()
                .filter(orden -> orden.getEstado() != EstadoOrden.CANCELADO)
                .map(orden -> orden.getPrecioAcordado() != null ? orden.getPrecioAcordado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double tiempoPromedioReparacionDias = calcularTiempoPromedioReparacion(todasLasOrdenes);

        return new DashboardAdminResponse(
                activas,
                recibidas,
                enDiagnostico,
                enReparacion,
                listasParaRecoger,
                vencidas,
                sumaPreciosAcordados,
                tiempoPromedioReparacionDias
        );
    }

    public DashboardTecnicoResponse getDashboardTecnico(Long tecnicoId) {
        List<OrdenReparacion> misOrdenes = ordenRepo.findByTecnicoResponsableId(tecnicoId);
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximas = hoy.plusDays(3);

        long activas = misOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .count();

        long enReparacion = misOrdenes.stream()
                .filter(orden -> orden.getEstado() == EstadoOrden.EN_REPARACION)
                .count();

        long vencidas = misOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> orden.getFechaEstimadaEntrega().isBefore(hoy))
                .count();

        long proximasEntregas = misOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> !orden.getFechaEstimadaEntrega().isBefore(hoy))
                .filter(orden -> !orden.getFechaEstimadaEntrega().isAfter(limiteProximas))
                .count();

        long casosAtencionHoy = misOrdenes.stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> orden.getFechaEstimadaEntrega().isEqual(hoy))
                .count();

        return new DashboardTecnicoResponse(
                activas,
                enReparacion,
                vencidas,
                proximasEntregas,
                casosAtencionHoy
        );
    }

    public List<CargaTecnicoResponse> getCargaPorTecnico() {
        List<Usuario> tecnicosActivos = usuarioRepository.findByRol(RolUsuario.TECNICO)
                .stream()
                .filter(tecnico -> Boolean.TRUE.equals(tecnico.getActivo()))
                .toList();

        Map<Long, Long> cantidadOrdenesPorTecnico = ordenRepo.findAll()
                .stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getTecnicoResponsable() != null)
                .collect(Collectors.groupingBy(
                        orden -> orden.getTecnicoResponsable().getId(),
                        Collectors.counting()
                ));

        return tecnicosActivos.stream()
                .map(tecnico -> new CargaTecnicoResponse(
                        tecnico.getId(),
                        tecnico.getNombreCompleto(),
                        cantidadOrdenesPorTecnico.getOrDefault(tecnico.getId(), 0L)
                ))
                .sorted(
                        Comparator.comparingLong(CargaTecnicoResponse::cantidadOrdenesAsignadas)
                                .reversed()
                                .thenComparing(CargaTecnicoResponse::nombreTecnico)
                )
                .toList();
    }

    public List<ProximaEntregaResponse> getProximasEntregas() {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximas = hoy.plusDays(3);

        return ordenRepo.findAll()
                .stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> !orden.getFechaEstimadaEntrega().isBefore(hoy))
                .filter(orden -> !orden.getFechaEstimadaEntrega().isAfter(limiteProximas))
                .sorted(Comparator.comparing(OrdenReparacion::getFechaEstimadaEntrega))
                .map(orden -> new ProximaEntregaResponse(
                        orden.getId(),
                        orden.getTicket(),
                        orden.getFechaEstimadaEntrega(),
                        obtenerNombreCliente(orden),
                        orden.getEstado().name(),
                        orden.getPrioridad().name()
                ))
                .toList();
    }

    public List<OrdenVencidaResponse> getOrdenesVencidas() {
        LocalDate hoy = LocalDate.now();

        return ordenRepo.findAll()
                .stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> orden.getFechaEstimadaEntrega().isBefore(hoy))
                .sorted(Comparator.comparing(OrdenReparacion::getFechaEstimadaEntrega))
                .map(orden -> new OrdenVencidaResponse(
                        orden.getId(),
                        orden.getTicket(),
                        orden.getFechaEstimadaEntrega(),
                        ChronoUnit.DAYS.between(orden.getFechaEstimadaEntrega(), hoy),
                        obtenerNombreCliente(orden),
                        orden.getEstado().name()
                ))
                .toList();
    }

    private boolean esOrdenAbierta(OrdenReparacion orden) {
        return Boolean.TRUE.equals(orden.getActivo())
                && orden.getEstado() != EstadoOrden.ENTREGADO
                && orden.getEstado() != EstadoOrden.CANCELADO;
    }

    private long contarPorEstado(List<OrdenReparacion> ordenes, EstadoOrden estado) {
        return ordenes.stream()
                .filter(orden -> orden.getEstado() == estado)
                .count();
    }

    private double calcularTiempoPromedioReparacion(List<OrdenReparacion> ordenes) {
        return ordenes.stream()
                .filter(orden -> orden.getEstado() == EstadoOrden.ENTREGADO)
                .filter(orden -> orden.getFechaIngreso() != null)
                .filter(orden -> orden.getFechaEntrega() != null)
                .mapToLong(orden -> ChronoUnit.DAYS.between(
                        orden.getFechaIngreso().toLocalDate(),
                        orden.getFechaEntrega().toLocalDate()
                ))
                .average()
                .orElse(0.0);
    }

    private String obtenerNombreCliente(OrdenReparacion orden) {
        Cliente cliente = orden.getEquipo().getCliente();
        return (cliente.getNombres() + " " + cliente.getApellidos()).trim();
    }
}