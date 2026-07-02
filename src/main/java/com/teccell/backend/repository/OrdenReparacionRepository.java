package com.teccell.backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.enums.EstadoOrden;

public interface OrdenReparacionRepository extends JpaRepository<OrdenReparacion, Long> {

    @Query("SELECT o FROM OrdenReparacion o JOIN FETCH o.equipo e JOIN FETCH e.cliente WHERE o.ticket = :ticket")
    Optional<OrdenReparacion> findByTicket(@Param("ticket") String ticket);

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
    

    @Query("SELECT o FROM OrdenReparacion o JOIN FETCH o.tecnicoResponsable")
    List<OrdenReparacion> findAllConTecnicos();

    @Query("SELECT o FROM OrdenReparacion o JOIN FETCH o.tecnicoResponsable WHERE o.tecnicoResponsable.id = :id")
    List<OrdenReparacion> findByTecnicoResponsableIdConTecnico(@Param("id") Long id);

    @Query("SELECT SUM(o.precioAcordado) FROM OrdenReparacion o WHERE o.activo = true")
    BigDecimal sumarPreciosAcordados();

    @Query("SELECT COUNT(o) FROM OrdenReparacion o WHERE o.estado NOT IN (:excluidos) AND o.fechaEstimadaEntrega < CURRENT_DATE")
    long countVencidas(@Param("excluidos") List<EstadoOrden> excluidos);

    @Query("SELECT o FROM OrdenReparacion o WHERE o.fechaEstimadaEntrega >= CURRENT_DATE AND o.estado <> 'ENTREGADO'")
    List<OrdenReparacion> findProximasEntregas();

    @Query("SELECT o FROM OrdenReparacion o WHERE o.fechaEstimadaEntrega < CURRENT_DATE AND o.estado <> 'ENTREGADO'")
    List<OrdenReparacion> findVencidas();
    
    long countByEquipoId(Long equipoId);
    long countByEstado(EstadoOrden estado);
}