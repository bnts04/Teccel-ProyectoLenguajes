package com.teccell.backend.service;

import com.teccell.backend.dto.response.AlertaOrdenResponse;
import com.teccell.backend.dto.response.ReincidenciaEquipoResponse;
import com.teccell.backend.entity.Equipo;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.NivelReincidenciaEquipo;
import com.teccell.backend.enums.SituacionEntrega;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.EquipoRepository;
import com.teccell.backend.repository.OrdenReparacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlertaOrdenService {

    private final OrdenReparacionRepository ordenRepo;
    private final EquipoRepository equipoRepo;

    public AlertaOrdenService(
            OrdenReparacionRepository ordenRepo,
            EquipoRepository equipoRepo
    ) {
        this.ordenRepo = ordenRepo;
        this.equipoRepo = equipoRepo;
    }

    public List<AlertaOrdenResponse> getOrdenesVencidas() {
        LocalDate hoy = LocalDate.now();

        return ordenRepo.findAll()
                .stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> orden.getFechaEstimadaEntrega().isBefore(hoy))
                .sorted(Comparator.comparing(OrdenReparacion::getFechaEstimadaEntrega))
                .map(orden -> mapearAlertaResponse(orden, hoy, SituacionEntrega.VENCIDA))
                .toList();
    }

    public List<AlertaOrdenResponse> getProximasEntregas() {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximas = hoy.plusDays(3);

        return ordenRepo.findAll()
                .stream()
                .filter(this::esOrdenAbierta)
                .filter(orden -> orden.getFechaEstimadaEntrega() != null)
                .filter(orden -> !orden.getFechaEstimadaEntrega().isBefore(hoy))
                .filter(orden -> !orden.getFechaEstimadaEntrega().isAfter(limiteProximas))
                .sorted(Comparator.comparing(OrdenReparacion::getFechaEstimadaEntrega))
                .map(orden -> {
                    long diasRestantes = ChronoUnit.DAYS.between(hoy, orden.getFechaEstimadaEntrega());

                    SituacionEntrega situacion = diasRestantes == 0
                            ? SituacionEntrega.VENCE_HOY
                            : SituacionEntrega.PROXIMA_A_VENCER;

                    return mapearAlertaResponse(orden, hoy, situacion);
                })
                .toList();
    }

    public ReincidenciaEquipoResponse calcularReincidencia(Long equipoId) {
        Equipo equipo = equipoRepo.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el equipo con ID: " + equipoId));

        long reparacionesAnteriores = ordenRepo.findByEquipoId(equipoId)
                .stream()
                .filter(orden -> orden.getEstado() == EstadoOrden.ENTREGADO)
                .count();

        NivelReincidenciaEquipo nivel;

        if (reparacionesAnteriores == 0) {
            nivel = NivelReincidenciaEquipo.SIN_ANTECEDENTES;
        } else if (reparacionesAnteriores == 1) {
            nivel = NivelReincidenciaEquipo.CON_ANTECEDENTE;
        } else {
            nivel = NivelReincidenciaEquipo.REINCIDENTE;
        }

        String marcaModelo = equipo.getMarca() + " " + equipo.getModelo();

        return new ReincidenciaEquipoResponse(
                equipo.getId(),
                equipo.getCodigoInterno(),
                marcaModelo,
                reparacionesAnteriores,
                nivel
        );
    }

    private boolean esOrdenAbierta(OrdenReparacion orden) {
        return Boolean.TRUE.equals(orden.getActivo())
                && orden.getEstado() != EstadoOrden.ENTREGADO
                && orden.getEstado() != EstadoOrden.CANCELADO;
    }

    private AlertaOrdenResponse mapearAlertaResponse(
            OrdenReparacion orden,
            LocalDate hoy,
            SituacionEntrega situacion
    ) {
        long diasDiferencia = ChronoUnit.DAYS.between(hoy, orden.getFechaEstimadaEntrega());

        String marcaModelo = orden.getEquipo().getMarca() + " " + orden.getEquipo().getModelo();

        String clienteNombre = orden.getEquipo().getCliente().getNombres()
                + " "
                + orden.getEquipo().getCliente().getApellidos();

        return new AlertaOrdenResponse(
                orden.getId(),
                orden.getTicket(),
                marcaModelo,
                clienteNombre,
                orden.getFechaEstimadaEntrega(),
                diasDiferencia,
                situacion
        );
    }
}