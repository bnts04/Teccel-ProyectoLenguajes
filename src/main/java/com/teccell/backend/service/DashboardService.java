package com.teccell.backend.service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teccell.backend.dto.response.CargaTecnicoResponse;
import com.teccell.backend.dto.response.DashboardAdminResponse;
import com.teccell.backend.dto.response.DashboardTecnicoResponse;
import com.teccell.backend.dto.response.OrdenVencidaResponse;
import com.teccell.backend.dto.response.ProximaEntregaResponse;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.repository.OrdenReparacionRepository;


@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final OrdenReparacionRepository ordenRepo;

    public DashboardService(OrdenReparacionRepository ordenRepo) {
        this.ordenRepo = ordenRepo;
    }
    
    @Transactional(readOnly = true)
public List<CargaTecnicoResponse> getCargaPorTecnico() {

    return ordenRepo.findAllConTecnicos().stream()
        .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO
                  && o.getEstado() != EstadoOrden.CANCELADO)
        .filter(o -> o.getTecnicoResponsable() != null)
        .collect(Collectors.groupingBy(
            o -> o.getTecnicoResponsable().getNombreCompleto(),
            Collectors.counting()
        ))
        .entrySet().stream()
        .map(entry -> new CargaTecnicoResponse(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
}
    @Transactional(readOnly = true)
    public DashboardTecnicoResponse getDashboardTecnico(Long tecnicoId) {
        var misOrdenes = ordenRepo.findByTecnicoResponsableIdConTecnico(tecnicoId);

        long activas = misOrdenes.stream()
            .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO && o.getEstado() != EstadoOrden.CANCELADO)
            .count();

        long enReparacion = misOrdenes.stream()
            .filter(o -> o.getEstado() == EstadoOrden.EN_REPARACION)
            .count();

        return new DashboardTecnicoResponse(activas, enReparacion, 0, 0, 0); // Ajusta según necesites
    }

    public DashboardAdminResponse getDashboardAdmin() {
        var todasLasOrdenes = ordenRepo.findAll();
        LocalDate hoy = LocalDate.now();

        
        long activas = todasLasOrdenes.stream()
            .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO && o.getEstado() != EstadoOrden.CANCELADO).count();

        long vencidas = todasLasOrdenes.stream()
            .filter(o -> o.getEstado() != EstadoOrden.ENTREGADO && o.getEstado() != EstadoOrden.CANCELADO
                    && o.getFechaEstimadaEntrega().isBefore(hoy)).count();

        BigDecimal totalDinero = todasLasOrdenes.stream()
            .map(o -> o.getPrecioAcordado() != null ? o.getPrecioAcordado() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

      
        return new DashboardAdminResponse(
            activas,
            ordenRepo.countByEstado(EstadoOrden.RECIBIDO), // Necesitarás este método en el repo
            ordenRepo.countByEstado(EstadoOrden.EN_DIAGNOSTICO),
            ordenRepo.countByEstado(EstadoOrden.EN_REPARACION),
            ordenRepo.countByEstado(EstadoOrden.LISTO_PARA_RECOGER),
            vencidas,
            totalDinero,
            0.0
        );
    }

public List<ProximaEntregaResponse> getProximasEntregas() {
    return ordenRepo.findProximasEntregas().stream()
            .filter(orden -> orden.getFechaEntrega() != null) 
            .map(orden -> new ProximaEntregaResponse(
                    orden.getId(), 
                    orden.getFechaEntrega().toLocalDate(), 
                    orden.getEquipo().getCliente().getNombres()
            )).toList();
}
public List<OrdenVencidaResponse> getOrdenesVencidas() {
    return ordenRepo.findVencidas().stream()
        .map(orden -> new OrdenVencidaResponse(
            orden.getId(),
            orden.getFechaEntrega().toLocalDate(),
            orden.getEstado().name()
        ))
        .toList();
}
}
