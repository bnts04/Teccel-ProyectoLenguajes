package com.teccell.backend.repository;

import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenReparacionRepository extends JpaRepository<OrdenReparacion, Long> {

    Optional<OrdenReparacion> findByTicket(String ticket);

    boolean existsByTicket(String ticket);

    List<OrdenReparacion> findByTecnicoResponsableId(Long tecnicoId);

    List<OrdenReparacion> findByEstado(EstadoOrden estado);

    List<OrdenReparacion> findByEquipoId(Long equipoId);
}