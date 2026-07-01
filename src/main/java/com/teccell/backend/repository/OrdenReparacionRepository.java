package com.teccell.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;

public interface OrdenReparacionRepository extends JpaRepository<OrdenReparacion, Long> {

    Optional<OrdenReparacion> findByTicket(String ticket);

    boolean existsByTicket(String ticket);

    List<OrdenReparacion> findByTecnicoResponsableId(Long tecnicoId);

    List<OrdenReparacion> findByEstado(EstadoOrden estado);

    List<OrdenReparacion> findByEquipoId(Long equipoId);

    @Query("SELECT o FROM OrdenReparacion o WHERE o.equipo.cliente.telefono = :telefono AND o.activo = true")
    List<OrdenReparacion> findByClienteTelefono(@Param("telefono") String telefono);

    @Query("SELECT o FROM OrdenReparacion o WHERE o.equipo.cliente.dni LIKE %:ultimos4 AND o.activo = true")
    List<OrdenReparacion> findByClienteDniTerminaCon(@Param("ultimos4") String ultimos4);
    
    @Query("SELECT o FROM OrdenReparacion o WHERE o.equipo.cliente.dni LIKE %:ultimos4 AND o.equipo.cliente.telefono = :telefono AND o.activo = true")
    List<OrdenReparacion> findByClienteDniTerminaConAndTelefono(@Param("ultimos4") String ultimos4, @Param("telefono") String telefono);
}