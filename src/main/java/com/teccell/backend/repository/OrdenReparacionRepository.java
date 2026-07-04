package com.teccell.backend.repository;

import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenReparacionRepository extends JpaRepository<OrdenReparacion, Long>,
        JpaSpecificationExecutor<OrdenReparacion> {

    Optional<OrdenReparacion> findByTicket(String ticket);

    boolean existsByTicket(String ticket);

    List<OrdenReparacion> findByTecnicoResponsableId(Long tecnicoId);

    List<OrdenReparacion> findByEstado(EstadoOrden estado);

    List<OrdenReparacion> findByEquipoId(Long equipoId);

    @Query("""
            SELECT o
            FROM OrdenReparacion o
            JOIN o.equipo e
            JOIN e.cliente c
            WHERE c.telefono = :telefono
            AND o.activo = true
            ORDER BY o.fechaCreacion DESC
            """)
    List<OrdenReparacion> findByClienteTelefono(@Param("telefono") String telefono);

    @Query("""
            SELECT o
            FROM OrdenReparacion o
            JOIN o.equipo e
            JOIN e.cliente c
            WHERE c.dni IS NOT NULL
            AND c.dni LIKE CONCAT('%', :ultimos4)
            AND o.activo = true
            ORDER BY o.fechaCreacion DESC
            """)
    List<OrdenReparacion> findByClienteDniTerminaCon(@Param("ultimos4") String ultimos4);

    @Query("""
            SELECT o
            FROM OrdenReparacion o
            JOIN o.equipo e
            JOIN e.cliente c
            WHERE c.dni IS NOT NULL
            AND c.dni LIKE CONCAT('%', :ultimos4)
            AND c.telefono = :telefono
            AND o.activo = true
            ORDER BY o.fechaCreacion DESC
            """)
    List<OrdenReparacion> findByClienteDniTerminaConAndTelefono(
            @Param("ultimos4") String ultimos4,
            @Param("telefono") String telefono
    );
}