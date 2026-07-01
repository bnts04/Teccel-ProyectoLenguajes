package com.teccell.backend.repository;

import com.teccell.backend.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByClienteId(Long clienteId);

    List<Equipo> findByClienteIdAndActivoTrue(Long clienteId);

    Optional<Equipo> findByCodigoInterno(String codigoInterno);

    boolean existsByCodigoInterno(String codigoInterno);
}