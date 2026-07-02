package com.teccell.backend.service; 
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teccell.backend.dto.response.AlertaOrdenResponse;
import com.teccell.backend.dto.response.ReincidenciaEquipoResponse;
import com.teccell.backend.entity.Equipo;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.NivelReincidenciaEquipo;
import com.teccell.backend.enums.SituacionEntrega;
import com.teccell.backend.repository.EquipoRepository;
import com.teccell.backend.repository.OrdenReparacionRepository;


@Service
@Transactional(readOnly = true)
public class AlertaOrdenService {

    private final OrdenReparacionRepository ordenRepo;
    private final EquipoRepository equipoRepo;

    public AlertaOrdenService(OrdenReparacionRepository ordenRepo, EquipoRepository equipoRepo) {
        this.ordenRepo = ordenRepo;
        this.equipoRepo = equipoRepo;
    }

    
    public List<AlertaOrdenResponse> getOrdenesVencidas() {
        LocalDate hoy = LocalDate.now();
        
        return ordenRepo.findAll().stream()
                .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO && o.getEstado() != EstadoOrden.CANCELADO)
                .filter(o -> o.getFechaEstimadaEntrega().isBefore(hoy))
                .map(o -> mapearAlertaResponse(o, hoy, SituacionEntrega.VENCIDA))
                .collect(Collectors.toList());
    }


    public List<AlertaOrdenResponse> getProximasEntregas() {
        LocalDate hoy = LocalDate.now();

        return ordenRepo.findAll().stream()
                .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO && o.getEstado() != EstadoOrden.CANCELADO)
                .filter(o -> !o.getFechaEstimadaEntrega().isBefore(hoy))
                .map(o -> {
                    long diasRestantes = ChronoUnit.DAYS.between(hoy, o.getFechaEstimadaEntrega());
                    SituacionEntrega situacion;

                    if (diasRestantes == 0) {
                        situacion = SituacionEntrega.VENCE_HOY;
                    } else if (diasRestantes >= 1 && diasRestantes <= 3) {
                        situacion = SituacionEntrega.PROXIMA_A_VENCER;
                    } else {
                        situacion = SituacionEntrega.DENTRO_DEL_PLAZO;
                    }

                  
                    return mapearAlertaResponse(o, hoy, situacion);
                })
                .collect(Collectors.toList());
    }

    // 3. Calcular reincidencia de un equipo por su ID
public ReincidenciaEquipoResponse calcularReincidencia(Long equipoId) {
        Equipo equipo = equipoRepo.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("No se encontró el equipo con ID: " + equipoId));

        long reparacionesAnteriores = ordenRepo.countByEquipoId(equipoId);
        
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

    private AlertaOrdenResponse mapearAlertaResponse(OrdenReparacion o, LocalDate hoy, SituacionEntrega situacion) {
        long diasDiferencia = ChronoUnit.DAYS.between(hoy, o.getFechaEstimadaEntrega());
        String marcaModelo = o.getEquipo().getMarca() + " " + o.getEquipo().getModelo();
        String clienteNombre = o.getEquipo().getCliente().getNombres() + " " + o.getEquipo().getCliente().getApellidos();

        return new AlertaOrdenResponse(
                o.getId(),
                o.getTicket(),
                marcaModelo,
                clienteNombre,
                o.getFechaEstimadaEntrega(),
                diasDiferencia,
                situacion
        );
    }
}